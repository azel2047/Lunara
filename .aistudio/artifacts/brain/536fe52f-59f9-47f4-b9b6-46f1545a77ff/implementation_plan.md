# Lunara Care - Menstrual Cycle Tracker & Reproductive Health AI

Lunara Care is a mobile-first menstrual cycle and reproductive wellness tracker built natively in Kotlin and Jetpack Compose. It pairs warm, claymorphic aesthetics with bioscience tracking (hormonal curves, 28-dot cycle matrix, symptom logging) and an intelligent Gemini AI Cycle Companion with Google Search Grounding and High Thinking capabilities.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were confirmed during the interactive clarification phase:

- **Confirmed Decision 1 (Runtime Architecture)**: Built as a native Android application using Jetpack Compose and Material 3, matching the typography, colors, 8px grid, and soft pink shadows from the design specifications.
- **Confirmed Decision 2 (Data Persistence & Security)**: Powered by Firebase Firestore with Google Sign-In via Jetpack Credential Manager and local offline caching, alongside client-side export options (PDF and encrypted JSON).
- **Confirmed Decision 3 (AI Placement & Role)**: A dedicated **Wawasan & AI Konselor** tab with multi-turn chat, utilizing `gemini-4-argon` (with `thinkingLevel = HIGH`) for complex hormonal and bioscience reasoning, and `gemini-3.8-flash` with Google Search Grounding for current health queries.

---

## 1. Overview & Core Concept

- **What It Does**: Lunara Care empowers women to log cycle dates, flow intensities, and physical/mood symptoms, providing algorithmic estimations for upcoming periods, fertile windows, and PMS phases. All predictions are transparently labeled as estimations ("estimasi") with medical disclaimer notices.
- **Target Audience / Persona**: Individuals seeking a private, empathetic, aesthetically soothing menstrual tracker with scientifically accurate insights into their biological rhythms.
- **Key Value**: Combines cycle calculations (6-cycle weighted rolling average) with deep hormonal education (Estrogen, Progesterone, LH surge curves) and immediate consultation via an AI cycle assistant.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Beranda (Home Dashboard)**:
   - Header with greeting ("Halo, Sasa ✨"), mood prompt, and privacy shield status.
   - Hero Cycle Card featuring active cycle day (e.g., "Hari ke-14"), phase badge ("Fase Folikuler"), countdown to next period, and an animated glossy 3D clay droplet.
   - Quick action pills for "Catat Haid", "Gejala", and "Mood".
   - 2x2 Wellness tiles: Water intake (Air Minum), Sleep (Tidur), Workout (Olahraga), and Daily Notes (Catatan).
   - "Tips Energi Hari Ini" educational care card.

2. **Kalender (Calendar & 28-Dot Cycle Matrix)**:
   - Month/Week toggle with month navigation (Oktober 2024).
   - Interactive 28-dot cycle visualizer (Menstruation, Follicular, Fertile Peak, and Luteal/PMS color-coded beads).
   - Full monthly calendar grid showing period indicators, ovulation markers, and expected next periods.
   - Cycle statistics card (Next period date, Average cycle length, Period duration) and phase guidance.
   - Floating primary CTA: "Catat Siklus & Gejala".

3. **Catat Siklus & Gejala (Bottom Sheet Modal)**:
   - Header with selected date, cycle day, and phase context chip.
   - Blood flow selector: Tidak Ada, Bercak, Ringan, Sedang, Deras.
   - Physical conditions multi-select grid with icons: Cramps (⚡ Kram Perut), Ovulation Fluid (💧 Cairan Bening), Sensitive Breasts (🌸 Payudara Sensitif), Headache (🤕 Sakit Kepala), Bloating (🎈 Kembung), Fatigue (💤 Lelah/Lemas), Backache (🧘 Nyeri Punggung), Clear Skin (✨ Kulit Bersih).
   - Mood selector: Bahagia, Tenang, Sensitif, Cemas, Lelah.
   - Sexual intimacy tracker: Gairah Tinggi, Sedang, Rendah, Terlindungi.
   - Private encrypted notes field with character counter.

4. **Wawasan & AI Konselor (Insights & Trends)**:
   - Time window filter: Bulan Ini, 3 Bulan, 6 Bulan.
   - Biological hormone fluctuation SVG/Canvas curve visualizing Estrogen, Progesterone, and LH surge across the 4 phases with an interactive day-14 marker.
   - Daily biological breakdown ("Apa yang Terjadi di Tubuhmu?") with hormone star ratings.
   - Symptom frequency & sleep duration correlation charts.
   - Integrated Gemini AI Reproductive Health Counselor with Search Grounding and High Thinking mode for evidence-based answers.

5. **Profil & Keamanan (Profile & Privacy)**:
   - User profile identity with Google Sign-In status and local encryption indicator.
   - Personal cycle settings: Cycle length (default 28 days), Period length (default 5 days).
   - Security switches: Biometric app lock, screen blur privacy shield, disguised notifications.
   - Data ownership controls: Export Data Sheet (PDF / AES-256 JSON with password protection), Cloud sync trigger, and cycle history deletion.

### Visual Identity & Theme Tokens

- **Palette**:
  - `Canvas / Surface`: Off-white `#FFFAFC`, Surface `#FCF8FF`
  - `Primary Pink Strong`: `#D2386A`, `Primary Text`: `#C92A5E`, `Primary M3`: `#AA2E57`
  - `Pink Subtle`: `#FFD6E2`, `Pink Wash`: `#FFF1F5`, `Pink Tint`: `#FFE4EC`
  - `Fertile Lavender`: `#9C8CF0`, `Lavender Subtle`: `#E9E5FD`
  - `PMS Peach`: `#FFB88A`, `Peach Subtle`: `#FFEBDD`
  - `Ink`: `#22222B`, `Ink Muted`: `#6E6E7A`
- **Typography**: Google Font **Plus Jakarta Sans** (Weights: Regular 400, Medium 500, SemiBold 600, Bold 700) integrated locally.
- **Atmosphere & Depth**: Soft pink glow shadows (`0 8px 24px rgba(240, 100, 140, 0.15)`), inner specular highlights, and pill-shaped interactive components.

---

## 3. Key Product Decisions & Trade-Offs

- **Cycle Calculation Algorithm**:
  - Computes weighted rolling averages over the last 6 recorded cycles.
  - Falls back gracefully to standard physiological defaults (28-day cycle, 5-day menstruation) when insufficient data is present.
  - Ovulation day is estimated as 14 days prior to the predicted next period.
  - Fertile window spans 5 days ending on the ovulation day.
  - PMS phase covers the final 5 days of the cycle.
- **Biomedical Disclaimers**: Every predictive UI element includes the label "(estimasi)" and displays the medical non-diagnostic footnote in Indonesian.
- **Gemini AI Architecture**:
  - Complex bioscience queries and hormone analysis: `gemini-4-argon` with high thinking level (`ThinkingLevel.HIGH`).
  - Real-time medical search queries: `gemini-3.8-flash` with Google Search tool.
  - Fast suggestions: `gemini-3.1-flash-lite-preview`.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│                      (Edge-to-Edge, Theme, Navigation)                 │
└────────────────────────────────────┬───────────────────────────────────┘
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         ▼                           ▼                           ▼
  ┌───────────────┐           ┌───────────────┐           ┌───────────────┐
  │ BerandaScreen │           │ KalenderScreen│           │ WawasanScreen │
  └───────┬───────┘           └───────┬───────┘           └───────┬───────┘
          │                           │                           │
          └───────────────────────────┼───────────────────────────┘
                                     │
                                     ▼
                      ┌──────────────────────────────┐
                      │    CycleViewModel (State)    │
                      └──────────────┬───────────────┘
                                     │
            ┌────────────────────────┴────────────────────────┐
            ▼                                                 ▼
  ┌───────────────────┐                             ┌───────────────────┐
  │ FirestoreRepository│                             │   GeminiService   │
  │ (Cycles, Symptoms)│                             │ (Argon/Flash Chat)│
  └───────────────────┘                             └───────────────────┘
```

### Core Data Models

- **`CycleRecord`**: `id`, `userId`, `startDate`, `endDate`, `cycleLength`, `periodDuration`.
- **`DailyLog`**: `date`, `flowLevel` (`NONE`, `SPOTTING`, `LIGHT`, `MEDIUM`, `HEAVY`), `symptoms` (`List<Symptom>`), `mood` (`Mood`), `intimacy` (`IntimacyLevel`), `notes`, `waterIntakeMl`, `sleepHours`, `workoutMinutes`.
- **`UserProfileSettings`**: `cycleLengthDays`, `periodLengthDays`, `biometricEnabled`, `screenDisguiseEnabled`, `disguisedNotificationsEnabled`.
