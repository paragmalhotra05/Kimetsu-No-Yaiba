# Kimetsu No Yaiba - Premium Manga Reader App

A premium, secure Android application for reading Demon Slayer (Kimetsu no Yaiba) manga volumes with an immersive, controlled digital reading experience.

## 🎯 Features

### ✨ Premium Reading Experience
- **Secure PDF Viewer** - Read manga with smooth, responsive rendering
- **Multiple Reading Modes** - Day, Night, and Sepia modes for comfortable reading
- **Adaptive Zoom** - Double-tap to zoom, pinch to zoom functionality
- **Progress Tracking** - Automatic bookmark of your last read page
- **Manual Bookmarks** - Save your favorite pages for quick access
- **Brightness Control** - In-app brightness adjustment

### 🔒 Content Protection
- **Screenshot Prevention** - FLAG_SECURE prevents screenshots and screen recording
- **No Downloads** - Content cannot be exported or saved locally
- **Session-Based Viewing** - Secure streaming-style reading
- **DRM-Like Protection** - Files are accessed only through the app

### 📚 Library Management
- **Volume Grid View** - Beautiful card-based library interface
- **Reading Progress Indicators** - Visual progress bars on each volume
- **Continue Reading** - Quick resume from where you left off
- **Volume Information** - Page counts, descriptions, and release dates

### 🎨 Premium UI/UX
- **Material Design 3** - Modern, polished interface
- **Gradient Accents** - Demon Slayer themed red gradients
- **Smooth Animations** - Fluid transitions and interactions
- **Responsive Layout** - Adapts to different screen sizes

## 📱 Technical Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Material Components, ViewBinding
- **PDF Rendering**: Barteksc AndroidPdfViewer
- **Architecture**: MVVM-inspired with data managers
- **Storage**: SharedPreferences for progress/bookmarks
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

### Project Structure
```
app/src/main/java/kimetsu/no/yaiba/
├── MainActivity.kt              # Library/Home screen
├── ReaderActivity.kt            # PDF reading activity
├── models/
│   └── Models.kt               # Data models (Volume, Bookmark, etc.)
├── data/
│   └── VolumeManager.kt        # Business logic & data management
└── adapters/
    └── VolumeAdapter.kt        # RecyclerView adapter for volumes
```

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK with API 36

### Building the App

1. **Clone and Open**
   ```bash
   cd d:\KimetsuNoYaiba
   # Open in Android Studio
   ```

2. **Sync Gradle**
   - Let Android Studio download dependencies
   - This includes PDF viewer, Material Components, etc.

3. **Add PDF Files**
   
   To add actual manga PDFs, you have two options:
   
   **Option A: Assets Folder** (Recommended for demo)
   ```
   app/src/main/assets/
   ├── kimetsu_volume_01.pdf
   ├── kimetsu_volume_02.pdf
   ├── kimetsu_volume_03.pdf
   └── ... (add more volumes)
   ```
   
   **Option B: Internal Storage** (For production)
   - Implement backend API to stream PDFs
   - Download PDFs to app's internal storage
   - Files stored at: `context.filesDir/kimetsu_volume_XX.pdf`

4. **Build and Run**
   ```bash
   # Command line
   ./gradlew assembleDebug
   
   # Or in Android Studio
   Run > Run 'app'
   ```

## 📖 How to Use

### For Users

1. **Launch App** - Opens library with all volumes
2. **Select Volume** - Tap any volume card to start reading
3. **Reading Controls**:
   - **Tap center** - Toggle controls visibility
   - **Swipe** - Navigate between pages
   - **Double tap** - Zoom in/out
   - **Mode button** - Switch reading modes
   - **Bookmark button** - Add/remove bookmarks
   - **Brightness slider** - Adjust screen brightness

### For Developers

#### Adding New Volumes

Edit `VolumeManager.kt` and add to the `getAllVolumes()` function:

```kotlin
Volume(
    id = 9,
    volumeNumber = 9,
    title = "Your Volume Title",
    description = "Volume description...",
    coverImageUrl = "volume_9_cover",
    totalPages = 192,
    pdfFileName = "kimetsu_volume_09.pdf",
    releaseDate = "Month Day, Year"
)
```

#### Implementing Backend PDF Streaming

Replace `loadPDF()` in `ReaderActivity.kt`:

```kotlin
private fun loadPDF() {
    // Fetch from secure API
    lifecycleScope.launch {
        val pdfBytes = apiService.getVolumePDF(volumeId)
        val pdfFile = File(filesDir, "temp_${volumeId}.pdf")
        pdfFile.writeBytes(pdfBytes)
        
        binding.pdfView.fromFile(pdfFile)
            .load()
    }
}
```

#### Custom Volume Covers

Replace placeholder with actual images:
```
app/src/main/res/drawable/
├── volume_1_cover.png
├── volume_2_cover.png
└── ...
```

Update `VolumeAdapter.kt`:
```kotlin
// Load actual covers
Glide.with(binding.root.context)
    .load(getResourceId(volume.coverImageUrl))
    .into(binding.volumeCover)
```

## 🔐 Security Features

### Screenshot Prevention
```kotlin
window.setFlags(
    WindowManager.LayoutParams.FLAG_SECURE,
    WindowManager.LayoutParams.FLAG_SECURE
)
```

### Content Access Control
- PDFs are stored in app's private directory (`filesDir`)
- No external storage access
- Files deleted when app is uninstalled
- No sharing or export functionality

### Future Enhancements
- [ ] Server-side DRM with license validation
- [ ] Time-limited access tokens
- [ ] User authentication
- [ ] Analytics for reading behavior
- [ ] Encrypted PDF storage
- [ ] Watermarking

## 🎨 Customization

### Change Theme Colors

Edit `values/colors.xml`:
```xml
<color name="primary_red">#DC143C</color>  <!-- Main brand color -->
<color name="primary_dark">#8B0000</color> <!-- Darker variant -->
```

### Modify Reading Modes

Add new modes in `Models.kt`:
```kotlin
enum class ReadingMode {
    DAY,
    NIGHT,
    SEPIA,
    CUSTOM  // Add your mode
}
```

Implement in `ReaderActivity.kt`:
```kotlin
ReadingMode.CUSTOM -> {
    binding.root.setBackgroundColor(yourCustomColor)
}
```

## 📊 Data Storage

### Reading Progress
- Stored in SharedPreferences
- Keys: `progress_{volumeId}_page`, `progress_{volumeId}_percent`
- Persists across app sessions

### Bookmarks
- Stored in SharedPreferences
- Keys: `bookmark_count_{volumeId}`, `bookmark_{volumeId}_{index}_*`
- Unlimited bookmarks per volume

### Preferences
- Reading mode: `reading_mode`
- Last brightness: Window parameter (session only)

## 🐛 Troubleshooting

### "PDF not found" Error
- Ensure PDF files are in `assets` folder or `filesDir`
- Check filename matches exactly (case-sensitive)

### Build Errors
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### PDF Not Rendering
- Check PDF is not corrupted
- Ensure file permissions are correct
- Try a different PDF file

### Controls Not Showing
- Tap the center of the screen
- Check if controls are animated off-screen

## 📝 License

This is a demonstration project for premium manga reading experience.

**Important**: This app is designed to work with legally owned manga content. Users must have proper licenses/rights to the content they view.

## 🙏 Credits

- **PDF Viewer**: [AndroidPdfViewer by barteksc](https://github.com/barteksc/AndroidPdfViewer)
- **Design**: Material Design 3 by Google
- **Manga**: Demon Slayer (Kimetsu no Yaiba) by Koyoharu Gotouge

## 📧 Support

For issues or questions:
1. Check the troubleshooting section
2. Review the code comments
3. Examine the implementation examples

---

**Built with ❤️ for Demon Slayer fans**
