# SOMNIO - The AI Dream Social Network 🌌

Traditional social platforms share reality. **Somnio shares imagination.**  
Somnio is a premium, high-impact native Android application built using **Kotlin**, **Jetpack Compose**, and **Material Design 3**. It transforms user-submitted raw dream descriptions into beautiful, cohesive, and immersive AI-generated text, psychological interpretations, and gorgeous dynamic visual waves.

---

## 🎨 Creative & Aesthetic Direction

Somnio features a customized, top-tier futuristic design system:
- **Interstellar Dark Mode**: Forced default pure pitch-black canvases (`0xFF07070B`) for visual rest and high contrast.
- **Glowing Neon Colors**: Custom palette leveraging vibrant **Cyber Pink** (`0xFFEC4899`), cosmic **Dream Purple** (`0xFF8B5CF6`), and electrical **Nebula Cyan** (`0xFF06B6D4`).
- **Glassmorphism Layering**: Transparent cards with subtle glowing neon border brush trims to establish structural depth.
- **Procedural Canvas Drawings**: Fully reactive, animated mathematical stardust loops drawn directly on Compose canvases based on randomized wave generators.

---

## 🧠 Core Architecture (MVVM & Clean Architecture)

The codebase is engineered with strict modular abstraction:

1. **Entities & Persistence (`com.example.data.Models`)**:
   - Local SQLite database managed via **Room** encapsulating accounts, dream streams, comments/replies, notifications, persistent chambers, and telepathic chat threads.
2. **Data Access Layer (`com.example.data.AppDatabase`)**:
   - Custom reactive, flow-driven DAOs ensuring immediate, single-source-of-truth UI updates.
3. **Network Layer (`com.example.data.GeminiNetwork`)**:
   - Implements direct Retrofit HTTP services powered by **Moshi** matching standard Gemini API REST endpoints.
4. **Repository Layer (`com.example.data.DreamRepository`)**:
   - Handles the orchestration between SQLite database and actual Gemini REST engines.
   - **Zero-Dead-End Fallback Mapping**: Generates high-fidelity alternate dreamscapes, story structures, and hashtags locally if the API tokens are empty or unpaired.
5. **UI & State Layer (`com.example.ui.DreamViewModel`)**:
   - Extends Android Lifecycle ViewModel implementing reactive coroutine state-flows.

---

## 🚀 Key User Workflows

- **Sanctuary Feed**: Infinite scrolling grid of dreams from the explorer collective, incorporating real-time likes, bookmarking, and discussion comments.
- **AI Dreamplex Creation**: Enter direct descriptions. Deconstruct them via live Gemini calls into structured stories, tag sets, ambient track labels, and alternative choice branches.
- **Chamber Communities**: Align and join distinct collaborative dream space aggregates.
- **Creator Studio**: Access local coordinate metrics, monitor telemetries, and load draft sandboxes into active converters.
- **Nexus Admin Panel**: Roles management, modular AI provider toggles, and direct user supervision.

---

## ⚡ Setup & Credentials

Somnio manages credentials securely via the **Secrets Gradle Plugin** and `.env` files:
1. Open the **Secrets Panel** in Google AI Studio.
2. Enter your `GEMINI_API_KEY`.
3. Build & Run the streaming emulator in the browser!
