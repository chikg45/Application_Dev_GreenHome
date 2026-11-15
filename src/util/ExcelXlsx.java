package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Doc worksheet dau tien cua file Excel .xlsx bang JDK (khong can Apache POI).
 * Cot la cac header ten field o dong 1. Ho tro sharedStrings, inlineStr va so.
 * Gioi han kich thuoc de khong doc cac workbook qua lon/khong tin cay.
 */
public final class ExcelXlsx {
    private ExcelXlsx() { }
    private static final int GIOI_HAN_DONG = 5000;
    private static final long GIOI_HAN_FILE = 10L * 1024 * 1024;
    private static final long GIOI_HAN_XML = 24L * 1024 * 1024;

    public record Dong(int soDong, Map<String, String> giaTri) {
        public String lay(String tenCot) {
            return giaTri.getOrDefault(chuanHoa(tenCot), "").trim();
        }
        public String batBuoc(String tenCot) {
            String giaTri = lay(tenCot);
            if (giaTri.isEmpty()) throw new IllegalArgumentException(
                    "Dòng " + soDong + ": thiếu " + tenCot + ".");
            return giaTri;
        }
    }

    public static List<Dong> doc(Path file, String... cotBatBuoc) {
        if (file == null || !file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".xlsx"))
            throw new IllegalArgumentException("Chỉ hỗ trợ file Excel .xlsx (không phải .xls).");
        try {
            if (Files.size(file) > GIOI_HAN_FILE)
                throw new IllegalArgumentException("File Excel vượt quá 10 MB.");
            try (ZipFile zip = new ZipFile(file.toFile())) {
                List<String> strings = new ArrayList<>();
                ZipEntry shared = zip.getEntry("xl/sharedStrings.xml");
                if (shared != null) {
                    Document doc = taiXml(zip, shared);
                    for (Element si : cac(doc.getDocumentElement(), "si")) strings.add(ghepText(si));
                }
                ZipEntry worksheet = zip.getEntry("xl/worksheets/sheet1.xml");
                if (worksheet == null) {
                    worksheet = zip.stream().filter(e -> e.getName().matches("xl/worksheets/sheet[0-9]+\\.xml"))
                            .findFirst().orElse(null);
                }
                if (worksheet == null) throw new IllegalArgumentException("Không tìm thấy sheet dữ liệu trong Excel.");
                Document doc = taiXml(zip, worksheet);
                List<Dong> result = new ArrayList<>();
                List<String> headers = null;
                for (Element row : cac(doc.getDocumentElement(), "row")) {
                    int soDong = Integer.parseInt(row.getAttribute("r"));
                    Map<Integer, String> cells = new HashMap<>();
                    for (Element cell : cacTrucTiep(row, "c")) {
                        String ref = cell.getAttribute("r");
                        int idx = chiSoCot(ref);
                        if (idx >= 70) throw new IllegalArgumentException("File Excel có quá nhiều cột.");
                        if (!cacTrucTiep(cell, "f").isEmpty())
                            throw new IllegalArgumentException("Dòng " + soDong + ", cột " + ref
                                    + ": không dùng công thức; hãy dán giá trị trước khi import.");
                        String type = cell.getAttribute("t");
                        String value;
                        if ("inlineStr".equals(type)) value = ghepText(cell);
                        else {
                            String raw = giaTri(cell, "v");
                            if ("s".equals(type)) {
                                int index = Integer.parseInt(raw);
                                if (index < 0 || index >= strings.size())
                                    throw new IllegalArgumentException("Chỉ số shared string sai tại " + ref);
                                value = strings.get(index);
                            } else value = raw;
                        }
                        cells.put(idx, value == null ? "" : value.trim());
                    }
                    if (headers == null) {
                        if (soDong != 1) throw new IllegalArgumentException("Dòng 1 của Excel phải là tên cột.");
                        int max = cells.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1);
                        headers = new ArrayList<>();
                        for (int i = 0; i <= max; i++) headers.add(chuanHoa(cells.getOrDefault(i, "")));
                        java.util.Set<String> tenCot = new java.util.HashSet<>();
                        for (String h : headers) {
                            if (!h.isBlank() && !tenCot.add(h))
                                throw new IllegalArgumentException("Tên cột Excel bị trùng: " + h);
                        }
                        for (String required : cotBatBuoc) {
                            if (!headers.contains(chuanHoa(required)))
                                throw new IllegalArgumentException("Thiếu cột '" + required + "' ở dòng tiêu đề Excel.");
                        }
                        continue;
                    }
                    if (cells.values().stream().allMatch(String::isBlank)) continue;
                    if (result.size() >= GIOI_HAN_DONG)
                        throw new IllegalArgumentException("Chỉ nhập tối đa 5.000 dòng mỗi lần.");
                    Map<String, String> values = new HashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        if (!headers.get(i).isBlank()) values.put(headers.get(i), cells.getOrDefault(i, ""));
                    }
                    result.add(new Dong(soDong, Map.copyOf(values)));
                }
                if (headers == null || result.isEmpty())
                    throw new IllegalArgumentException("Excel phải có dòng tiêu đề và ít nhất một dòng dữ liệu.");
                return List.copyOf(result);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Không đọc được Excel: " + e.getMessage(), e);
        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException e) {
            throw new IllegalArgumentException("File Excel không hợp lệ: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("File Excel chứa số chỉ mục không hợp lệ.", e);
        }
    }

    public static int soNguyen(Dong d, String col, int macDinh) {
        String s = d.lay(col);
        if (s.isEmpty()) return macDinh;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(
                "Dòng " + d.soDong() + ": '" + col + "' phải là số nguyên, không dùng dấu phẩy."); }
    }
    public static double soThucDuong(Dong d, String col) {
        try {
            double n = Double.parseDouble(d.batBuoc(col));
            if (!Double.isFinite(n) || n <= 0) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Dòng " + d.soDong() + ": '" + col
                    + "' phải là số lớn hơn 0, ví dụ 12990000 (không có dấu phân cách).");
        }
    }
    private static String chuanHoa(String s) {
        return s == null ? "" : s.replace("\ufeff", "").trim().toLowerCase(Locale.ROOT);
    }
    private static Document taiXml(ZipFile zip, ZipEntry e)
            throws IOException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
        if (e.getSize() > GIOI_HAN_XML) throw new IllegalArgumentException("Nội dung sheet Excel quá lớn.");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try (InputStream stream = zip.getInputStream(e)) {
            // Chặn zip bomb (nếu entry khai báo size không chính xác).
            byte[] xml = stream.readNBytes((int) GIOI_HAN_XML + 1);
            if (xml.length > GIOI_HAN_XML) throw new IllegalArgumentException("XML Excel quá lớn.");
            return factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
        }
    }
    private static List<Element> cac(Element root, String name) {
        List<Element> list = new ArrayList<>();
        NodeList nodes = root.getElementsByTagNameNS("*", name);
        for (int i=0; i<nodes.getLength(); i++) list.add((Element) nodes.item(i));
        return list;
    }
    private static List<Element> cacTrucTiep(Element root, String name) {
        List<Element> list = new ArrayList<>();
        for (org.w3c.dom.Node n=root.getFirstChild(); n!=null; n=n.getNextSibling()) {
            if (n instanceof Element el && name.equals(el.getLocalName())) list.add(el);
        }
        return list;
    }
    private static String giaTri(Element cell, String name) {
        List<Element> els = cacTrucTiep(cell, name);
        return els.isEmpty() ? "" : els.get(0).getTextContent();
    }
    private static String ghepText(Element e) {
        StringBuilder s = new StringBuilder();
        for (Element t : cac(e, "t")) s.append(t.getTextContent());
        return s.toString();
    }
    private static int chiSoCot(String ref) {
        int i=0, idx=0;
        while (i<ref.length() && Character.isLetter(ref.charAt(i))) {
            idx = idx*26 + (Character.toUpperCase(ref.charAt(i))-'A'+1);
            i++;
        }
        if (i==0) throw new IllegalArgumentException("Ô Excel sai địa chỉ: " + ref);
        return idx-1;
    }
}
