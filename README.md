ನಿಮ್ಮ ಗುರು — Nimma-Guru 🎓
> *"Every session taught is a seed of knowledge planted in the community."*
A GenAI-powered Community Mentorship Android App connecting retired professionals (Gurus) with students in Karnataka's tier-2/3 towns.
VTU Internship Program | MindMatrix | Project #18
---
📱 Screenshots
> Add screenshots here after running the app
---
✨ Features
Feature	Status
Google Sign-In (Firebase Auth)	✅
Guru Profile create/edit	✅
Skill-filter search with ChipGroups	✅
Real-time Firestore sync	✅
Class Calendar with sessions	✅
Appreciation Wall (Thank-You notes)	✅
Wall of Fame leaderboard	✅
AI Bio generation (Gemini API)	✅
AI Session tips (Gemini API)	✅
Bilingual UI — Kannada + English	✅
Elderly-friendly large fonts	✅
---
🛠 Tech Stack
Language: Kotlin
UI: Jetpack Compose + Material3
Architecture: MVVM + Repository pattern
Backend: Firebase Auth + Firestore
AI: Google Gemini 1.5 Flash API
Min SDK: API 26 (Android 8.0)
---
🚀 Setup Instructions
Prerequisites
Android Studio Hedgehog or newer
JDK 11+
A Google/Firebase account
A Gemini API key (free at aistudio.google.com)
Steps
Clone the repository
```bash
   git clone https://github.com/Hafizkhan123/nimma-guru.git
   cd nimma-guru
   ```
Set up Firebase
Go to console.firebase.google.com
Create a new project called "NimmaGuru"
Add an Android app with package `com.hafiz.nimmaguru`
Download `google-services.json` and place it in the `app/` folder
Enable Google Sign-In in Firebase Authentication
Create a Firestore Database (start in test mode)
Copy the Web Client ID from Firebase Console → Authentication → Sign-in method → Google
Update Web Client ID
In `app/src/main/res/values/strings.xml`, replace:
```xml
   <string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
   ```
Add your API keys
Create `local.properties` in the project root (already in .gitignore):
```properties
   sdk.dir=/path/to/your/Android/sdk
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
Apply Firestore Rules
Copy contents of `firestore.rules` into Firebase Console → Firestore → Rules
Build and Run
```bash
   # Open in Android Studio and click Run, or:
   ./gradlew assembleDebug
   ```
---
📁 Project Structure
```
app/src/main/java/com/nimmaguru/
├── NimmaGuruApp.kt          # Application class
├── MainActivity.kt          # Entry point
├── data/
│   ├── model/Models.kt      # Guru, ClassSession, ThankYouNote
│   └── repository/
│       ├── FirestoreRepository.kt   # All Firestore operations
│       └── GeminiRepository.kt     # AI bio & tip generation
├── navigation/NavGraph.kt   # Navigation + bottom bar
└── ui/
    ├── screens/
    │   ├── LoginScreen.kt
    │   ├── HomeScreen.kt
    │   ├── GuruDetailScreen.kt
    │   ├── MyProfileScreen.kt
    │   ├── CalendarScreen.kt
    │   └── WallOfFameScreen.kt
    └── theme/Theme.kt       # Saffron-inspired Karnataka theme
```
---
🌍 Impact
SDG-4 Quality Education — Supplements school learning with expert community knowledge
Active Aging — Gives retirees a meaningful way to contribute
Gyaan-Daan — Builds a culture of voluntary Knowledge Donation
---
Built with ❤️ for Karnataka's communities
