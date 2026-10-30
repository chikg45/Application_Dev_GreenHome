## Cấu trúc branch

```text
Repository
├── main                 # Phiên bản cuối cùng, đã hoàn thiện và kiểm thử
├── develop              # Tích hợp code, theo dõi tiến độ và phát hiện lỗi
├── UI                   # Bộ khung giao diện chung của toàn nhóm
├── entity               # Các lớp thực thể và cấu trúc dữ liệu dùng chung
├── feature/C.Tai        # Chức năng C.Tai phụ trách
├── feature/Khoa         # Chức năng Khoa phụ trách
├── feature/Quy          # Chức năng Quy phụ trách
└── feature/T.Tai        # Chức năng T.Tai phụ trách
```

### `main` — Sản phẩm hoàn chỉnh

- Chỉ chứa phiên bản cuối cùng của ứng dụng để bàn giao, báo cáo và demo.
- **Không push trực tiếp code đang phát triển lên `main`.**
- Chỉ merge từ `develop` sau khi các chức năng đã được tích hợp, kiểm thử, sửa lỗi và nhóm thống nhất.

### `develop` — Phát triển và tích hợp

- Là nơi tập hợp code trong quá trình làm đồ án để theo dõi tiến độ của từng thành viên.
- Dùng để kiểm tra khả năng phối hợp giữa các chức năng, phát hiện và xử lý lỗi tích hợp.
- Thành viên push code lên branch cá nhân rồi tạo **Pull Request vào `develop`** để nhóm kiểm tra và merge.
- `develop` không nhất thiết luôn là bản hoàn chỉnh, nhưng cần được giữ ở trạng thái có thể kiểm tra và chạy thử khi có thể.

### `UI` — Khung giao diện thống nhất

- Chứa cấu trúc giao diện chung, bố cục, điều hướng và các thành phần dùng lại.
- **Tất cả thành viên phải bám theo bộ khung `UI`** khi phát triển chức năng của mình.
- Không tự ý thay đổi thiết kế chung nếu chưa trao đổi với cả nhóm.
- Các thay đổi của `UI` cần được tích hợp vào `develop` để mọi thành viên đồng bộ.

### `entity` — Mô hình dữ liệu chung

- Chứa các lớp Entity và khai báo dữ liệu cần được sử dụng thống nhất.
- Thành viên cần tái sử dụng Entity chung, tránh tự tạo các lớp trùng lặp.
- Mọi thay đổi thuộc tính hoặc quan hệ dữ liệu ảnh hưởng tới nhiều chức năng phải được thảo luận trước.

### `feature/*` — Branch theo thành viên

- Mỗi branch là nơi thành viên phát triển, chỉnh sửa và tự kiểm thử các chức năng được phân công.
- Commit thường xuyên, mô tả rõ công việc đã thực hiện.
- Khi có phần code sẵn sàng tích hợp, tạo Pull Request từ branch cá nhân vào `develop`.

##  Phân công và trách nhiệm

| Thành viên | Branch | Công việc |
|---|---|---|
| C.Tai | `feature/C.Tai` | Phát triển các chức năng Khuyến Mãi |
| Khoa | `feature/Khoa` | Phát triển các chức năng Quản lý  |
| Quy | `feature/Quy` | Phát triển các chức năng Thống kê|
| T.Tai | `feature/T.Tai` | Phát triển các chức năng Bán hàng |
| Cả nhóm | `UI`, `entity`, `develop` | Thống nhất giao diện, mô hình dữ liệu, tích hợp và kiểm thử |
| Cả nhóm | `main` | Xác nhận và bàn giao phiên bản cuối cùng |

> **Ghi chú:** Cập nhật thêm tên chức năng cụ thể của từng thành viên khi nhóm chốt phân công.

**Trách nhiệm chung:** hoàn thành chức năng được giao; tuân thủ giao diện `UI` và dữ liệu `entity`; commit/push đều đặn để nhóm theo dõi; tự kiểm thử trước khi tích hợp; phối hợp xử lý lỗi, xung đột và review code.

##  Quy trình làm việc

Đang cập nhật

##  Quy định chung

- **Không trực tiếp push lên `main`** trong quá trình phát triển.
- Ưu tiên tích hợp thông qua Pull Request để có lịch sử review và theo dõi tiến độ.
- Không tự ý chỉnh sửa phần việc của thành viên khác hoặc cấu trúc UI/Entity dùng chung.
- Đặt tên file, lớp, hàm, biến rõ ràng và theo coding convention thống nhất.
- Không commit file build/tạm không cần thiết, mật khẩu, khóa API hay thông tin nhạy cảm.
- Trước khi merge, kiểm tra lỗi và đảm bảo thay đổi không làm hỏng chức năng khác.

### Quy ước commit

| Tiền tố | Ý nghĩa |
|---|---|
| `feat:` | Thêm chức năng |
| `fix:` | Sửa lỗi |
| `refactor:` | Cải tiến cấu trúc code |
| `style:` | Điều chỉnh giao diện/định dạng |
| `docs:` | Cập nhật tài liệu |
| `test:` | Thêm hoặc sửa kiểm thử |
| `chore:` | Cấu hình, bảo trì |

##  Checklist trước khi hoàn thiện

- [ ] Tất cả thành viên đã hoàn thành chức năng được giao.
- [ ] Các chức năng được tích hợp vào `develop`.
- [ ] Toàn bộ giao diện tuân thủ khung `UI`.
- [ ] Các lớp Entity và dữ liệu dùng chung hoạt động thống nhất.
- [ ] Các chức năng chính đã được kiểm thử; không còn lỗi nghiêm trọng.
- [ ] Ứng dụng chạy ổn định, có thể demo.
- [ ] Cả nhóm thống nhất phiên bản cuối cùng trước khi merge vào `main`.

---

**Nguyên tắc nhóm:** `feature/*` để phát triển riêng → `develop` để tích hợp, theo dõi và sửa lỗi → `main` chỉ dành cho sản phẩm hoàn chỉnh. Tất cả thành viên phải bám theo `UI` và thống nhất với `entity`.
