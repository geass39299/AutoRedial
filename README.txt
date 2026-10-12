【用 GitHub 雲端編譯 APK】

1. 註冊/登入 https://github.com
2. 右上角 + → New repository,名稱填 AutoRedial,按 Create repository
3. 在新頁面點 "uploading an existing file",
   把解壓縮後「AutoRedial 資料夾裡的所有內容」拖進去(包含 app 資料夾、.github 資料夾),按 Commit changes
   * 若沒有看到 .github 資料夾:在儲存庫頁面按 Add file → Create new file,
     檔名輸入 .github/workflows/build.yml,
     貼上 build-workflow-備份.yml 的內容,按 Commit
4. 點上方 Actions 分頁,等「Build APK」出現綠色勾勾(約 3~6 分鐘)
5. 點進該次執行,頁面最下方 Artifacts 下載 AutoRedial-apk,解壓縮得到 app-debug.apk
6. 傳到手機,允許「安裝未知來源應用程式」後安裝
7. 開啟 App,允許所有權限;電池設定改為「不限制」

若 Actions 出現紅色叉叉,把錯誤訊息貼給 Claude。
