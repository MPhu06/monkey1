# Slide to Diagram

Ung dung web chuyen doi slide (PowerPoint `.pptx`) hoac noi dung text thanh cac so do hinh anh dep mat bang Mermaid.js.

## Cong nghe su dung

| Layer       | Cong nghe                          |
|-------------|------------------------------------|
| Frontend    | Vue 3 + Vite + TypeScript + Tailwind CSS |
| Backend     | Spring Boot 3 (Java 21)           |
| Database    | PostgreSQL 16                      |
| Diagram     | Mermaid.js                         |
| HTTP Client | Axios                              |

## Cau truc du an

```
d:\layout\
├── backend/                  # Spring Boot
│   ├── src/main/java/com/slidetodiagram/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── dto/
│   │   └── config/
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
├── frontend/                 # Vue 3
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── services/
│   │   ├── stores/
│   │   └── router/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── tailwind.config.js
├── SPEC.md
└── README.md
```

## Cac chuc nang chinh

- **[F1] Upload Slide**: Upload file `.pptx` qua drag-and-drop.
- **[F2] Nhap Text truc tiep**: Textarea de dan noi dung hoac go truc tiep.
- **[F3] Chon loai so do**: Flowchart, Sequence, Mind Map, ER, Class UML, Org Chart.
- **[F4] Generate & Preview**: Tu dong sinh Mermaid code va render thanh hinh anh.
- **[F5] Tai ve**: Download so do duoi dang PNG hoac SVG.
- **[F6] Lich su**: Luu lai cac lan convert trong database.

## Huong dan cai dat va chay

### Yeu cau he thong

- Java 21+
- Node.js 18+
- PostgreSQL 16+
- Maven 3.8+

### 1. Cai dat PostgreSQL

```sql
CREATE DATABASE slidetodiagram;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE slidetodiagram TO postgres;
```

Hoac chinh sua `application.yml` de phu hop voi cau hinh cua ban.

### 2. Chay Backend (Spring Boot)

```bash
cd backend

# Kiem tra Maven
mvn --version

# Build va chay
mvn spring-boot:run

# Hoac go lenh nay neu da co JAR
java -jar target/slide-to-diagram-backend-1.0.0.jar
```

Backend se chay tren **port 8080**.

### 3. Chay Frontend (Vue 3)

```bash
cd frontend

# Cai dat dependencies
npm install

# Chay che do phat trien
npm run dev

# Hoac build cho production
npm run build
```

Frontend se chay tren **port 5173** (dev).

### 4. Truy cap ung dung

Mo trinh duyet va truy cap: [http://localhost:5173](http://localhost:5173)

## API Endpoints

| Method | Endpoint                   | Mo ta                    |
|--------|----------------------------|--------------------------|
| GET    | `/api/health`              | Kiem tra he thong        |
| POST   | `/api/diagrams/generate`   | Tao diagram moi         |
| GET    | `/api/diagrams`            | Lay danh sach lich su    |
| GET    | `/api/diagrams/{id}`       | Lay chi tiet 1 diagram   |
| DELETE | `/api/diagrams/{id}`       | Xoa 1 diagram            |

### Vi du POST /api/diagrams/generate

```bash
curl -X POST http://localhost:8080/api/diagrams/generate \
  -F "inputType=text" \
  -F "inputText=B1: Dang nhap\nB2: Chon san pham\nB3: Thanh toan" \
  -F "diagramType=flowchart"
```

## Vi du nhap lieu

### Flowchart
```
Buoc 1: Dang nhap
Buoc 2: Kiem tra thong tin
Buoc 3: Neu thanh cong -> Hien thi dashboard
Buoc 3: Neu that bai -> Thong bao loi
```

### Sequence Diagram
```
Nguoi dung -> He thong: Gui yeu cau
He thong -> Co so du lieu: Truy van
Co so du lieu -> He thong: Tra ket qua
He thong -> Nguoi dung: Hien thi ket qua
```

### Mind Map
```
Marketing
  Noi dung
  Quang cao
  Mang xa hoi
San pham
  Phat trien
  Kiem thu
```

## Tuy chinh

### Doi Mermaid theme

Chinh sua trong `DiagramPreview.vue`:

```javascript
mermaid.initialize({
  theme: 'base',
  themeVariables: {
    primaryColor: '#4F46E5',
    primaryTextColor: '#1E293B',
    primaryBorderColor: '#6366F1',
    lineColor: '#94A3B8',
  }
})
```

### Doi cau hinh CORS

Chinh sua trong `backend/src/main/resources/application.yml`:

```yaml
app:
  cors:
    allowed-origins: http://localhost:5173,http://localhost:3000
```

## Giai quyet van de thuong gap

### Loi "Connection refused" khi goi API

Dam bao backend dang chay tren port 8080:

```bash
# Kiem tra port
netstat -an | grep 8080
```

### Loi parse PPTX

Dam bao file `.pptx` khong bi mat khau bao ve va co du lieu text.

### Khong hien thi duoc diagram

Kiem tra console trinh duyet de xem loi Mermaid render.

## Ban quyen

&copy; 2026 Slide to Diagram. Duoc tao voi yeu thuong va Mermaid.js.
