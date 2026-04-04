# Git Guide – WebBanTruyen Team

## Revert 1 commit

### Bước 1: Xem lịch sử commit
```cmd
git log --oneline
```
Kết quả ví dụ:
```
da3b437 feat: validate data and fix UI
8e7ef74 feat: add dashboard and order monitoring
0ac50d9 chore: add swagger, api docs and fix imports
```

### Bước 2: Revert commit cụ thể
```cmd
git revert <commit-hash>
```
Ví dụ:
```cmd
git revert da3b437
```

### Bước 3: Push lên remote
```cmd
git push
```

> `git revert` tạo commit mới để undo, KHÔNG xóa lịch sử. An toàn khi dùng chung với team.

---

## Các lệnh git thường dùng

### Xem trạng thái
```cmd
git status
git log --oneline
git branch
```

### Tạo nhánh mới
```cmd
git checkout -b feature/<ten-chuc-nang>
```

### Chuyển nhánh
```cmd
git switch <ten-nhanh>
```

### Cập nhật code mới nhất từ remote
```cmd
git pull origin <ten-nhanh>
```

### Merge nhánh khác vào nhánh hiện tại
```cmd
git merge <ten-nhanh>
```
Ví dụ đang ở feature branch, muốn lấy code mới từ dev:
```cmd
git merge dev
```

### Push lên remote
```cmd
git push
# Lần đầu push nhánh mới:
git push --set-upstream origin <ten-nhanh>
```

### Commit
```cmd
git add .
git commit -m "feat: mô tả ngắn"
```

---

## Convention commit message

| Prefix | Dùng khi |
|--------|----------|
| `feat` | Thêm tính năng mới |
| `fix` | Sửa bug |
| `chore` | Setup, config, không liên quan logic |
| `refactor` | Refactor code |
| `style` | Sửa UI/CSS |
| `docs` | Cập nhật tài liệu |

---

## Quy trình làm việc

1. Tạo nhánh từ `dev`: `git checkout -b feature/<ten>`
2. Code xong: `git add . && git commit -m "feat: ..."`
3. Lấy code mới nhất từ dev: `git merge dev`
4. Push: `git push`
5. Tạo Pull Request vào `dev` trên GitHub
