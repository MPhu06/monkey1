# Slide to Diagram — SPEC.md

## 1. Project Overview

- **Tên dự án**: Slide to Diagram
- **Loại**: Full-stack Web Application
- **Tổng quan**: Ứng dụng cho phép người dùng upload file slide (PowerPoint `.pptx`) hoặc nhập nội dung text, sau đó tự động chuyển đổi thành các sơ đồ hình ảnh (flowchart, sequence diagram, mind map, org chart, v.v.) sử dụng AI.
- **Người dùng mục tiêu**: Người dùng cần trình bày nội dung phức tạp dưới dạng sơ đồ trực quan.

## 2. Tech Stack

| Layer       | Technology                  |
|-------------|-----------------------------|
| Frontend    | Vue 3 + Vite + TypeScript   |
| Backend     | Spring Boot 3 (Java 21)     |
| Database    | PostgreSQL 16               |
| Diagram Lib | Mermaid.js                  |
| HTTP Client | Axios                       |
| Styling     | Tailwind CSS                |

## 3. Functionality Specification

### 3.1 Core Features

- **[F1] Upload Slide**: Upload file `.pptx` qua drag-and-drop hoặc file picker. Backend parse nội dung text từ slide.
- **[F2] Nhập Text trực tiếp**: Textarea để dán nội dung slide hoặc gõ trực tiếp.
- **[F3] Chọn loại sơ đồ**: Người dùng chọn loại diagram:
  - Flowchart (lưu đồ quy trình)
  - Sequence Diagram (sơ đồ tuần tự)
  - Mind Map (sơ đồ tư duy)
  - ER Diagram (sơ đồ thực thể)
  - Class Diagram (sơ đồ lớp UML)
  - Org Chart (sơ đồ tổ chức)
- **[F4] Generate & Preview Diagram**: Gọi API backend → parse nội dung → sinh Mermaid diagram code → render thành hình ảnh (SVG/PNG).
- **[F5] Tải về**: Download sơ đồ dưới dạng PNG hoặc SVG.
- **[F6] Lịch sử**: Lưu lại các lần convert gần đây trong database, cho phép xem lại.

### 3.2 User Flow

1. User mở ứng dụng → trang chủ với 2 tuỳ chọn: upload slide hoặc nhập text.
2. User nhập nội dung + chọn loại diagram.
3. User nhấn "Generate".
4. Backend xử lý, sinh Mermaid code, render diagram.
5. Frontend hiển thị diagram preview.
6. User có thể tải về hoặc tạo mới.

### 3.3 Data Model

```
DiagramJob {
  id          UUID (PK)
  input_type  VARCHAR(20)   -- "pptx" | "text"
  input_text  TEXT
  diagram_type VARCHAR(30)
  mermaid_code TEXT
  image_url   VARCHAR(500)
  status      VARCHAR(20)   -- "pending" | "completed" | "failed"
  created_at  TIMESTAMP
  updated_at  TIMESTAMP
}
```

### 3.4 API Endpoints

| Method | Endpoint                  | Mô tả                        |
|--------|---------------------------|------------------------------|
| POST   | `/api/diagrams/generate`  | Tạo diagram từ input         |
| GET    | `/api/diagrams`           | Lấy danh sách lịch sử        |
| GET    | `/api/diagrams/{id}`      | Lấy chi tiết 1 diagram       |
| DELETE | `/api/diagrams/{id}`      | Xoá 1 diagram                |
| GET    | `/api/health`             | Health check                 |

### 3.5 Request/Response Format

**POST /api/diagrams/generate**

Request:
```json
{
  "inputType": "text",
  "inputText": "Người dùng đăng nhập -> Hệ thống kiểm tra -> Đăng nhập thành công -> Hiển thị dashboard",
  "diagramType": "flowchart"
}
```

Response:
```json
{
  "id": "uuid",
  "mermaidCode": "flowchart TD\n  A[Người dùng đăng nhập] --> B[Hệ thống kiểm tra]\n  B --> C{Đăng nhập thành công?}\n  C -->|Có| D[Hiển thị dashboard]\n  C -->|Không| E[Thông báo lỗi]",
  "imageUrl": "/api/diagrams/{id}/image",
  "status": "completed"
}
```

## 4. UI/UX Design

### 4.1 Layout

- **Header**: Logo + navigation (Home, History)
- **Main**: Split layout — bên trái form nhập liệu, bên phải preview diagram
- **Footer**: Thông tin bản quyền

### 4.2 Pages

1. **Home** (`/`): Form upload/input + diagram type selector + generate button + preview
2. **History** (`/history`): Bảng danh sách các diagram đã tạo với thumbnail, thời gian, actions

### 4.3 Visual Design

- **Color Palette**:
  - Primary: `#4F46E5` (Indigo)
  - Secondary: `#10B981` (Emerald)
  - Background: `#F8FAFC`
  - Card: `#FFFFFF`
  - Text: `#1E293B`
  - Muted: `#64748B`
- **Typography**: Inter (Google Fonts)
- **Spacing**: 8px base grid
- **Border radius**: 8px cards, 6px buttons
- **Shadows**: Subtle box-shadow cho cards

## 5. Project Structure

```
d:\layout\
├── backend/                  # Spring Boot project
│   ├── src/main/java/com/slidetodiagram/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── repository/
│   │   └── config/
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── frontend/                 # Vue 3 project
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── services/
│   │   ├── stores/
│   │   └── router/
│   ├── index.html
│   └── package.json
├── SPEC.md
└── README.md
```

## 6. Acceptance Criteria

- [ ] Backend chạy trên port 8080, kết nối PostgreSQL thành công
- [ ] Frontend chạy trên port 5173 (dev) hoặc 80 (prod)
- [ ] Upload file `.pptx` parse được nội dung text
- [ ] Nhập text trực tiếp hoạt động
- [ ] Tất cả 6 loại diagram được generate đúng
- [ ] Preview diagram hiển thị đúng (Mermaid render)
- [ ] Tải PNG/SVG hoạt động
- [ ] Lịch sử lưu và hiển thị từ database
- [ ] Giao diện responsive, đẹp
