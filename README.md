<h1 align="center"> 🩺 FitIntel</h1>

<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=kotlin,androidstudio,firebase,materialui" />
  </a>
</p>

---

## 📌 Overview

**FitIntel** is a multi-module Android health assistant application. It allows users to authenticate securely, extract health data from uploaded PDF reports using on-device OCR, receive AI-driven health insights and track live running routes using Google Maps.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **Secure Authentication** | Provides email and password login/signup flows using Firebase Authentication. |
| 📄 **Smart PDF Processing** | Upload PDF reports, render pages, and extract health metrics using Google ML Kit (Text Recognition). |
| 🤖 **AI Health Insights** | Generates personalized health reports based on extracted data using Firebase AI (Gemini). |
| 📍 **Live Run Tracking** | Tracks location in real-time, draws route lines on Google Maps, and estimates steps taken. |
| 🔄 **Centralized Data Flow** | Shares processed health data and running stats instantly across different screens using a shared state. |
| 📊 **Home Dashboard** | Displays a clean summary of extracted health values and recent running activity. |

---

## 🏛️ Architecture

The project is built as a **Multi-Module Android Application** utilizing MVVM architecture and a shared singleton state (`AppState`) to pass data seamlessly between features.
```text
Root App Module     → Handles authentication state and main entry routing
       ↓
Feature Modules     → Isolated modules for Home, PDF, Gemini (AI), and Maps
       ↓
Core Modules        → Reusable modules for UI components, Navigation, and Data
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Authentication** | Firebase Authentication |
| **Artificial Intelligence** | Firebase AI (Gemini 2.5 Flash-Lite) |
| **Maps & Location** | Google Maps Compose + Google Play Services Location |
| **Machine Learning** | Google ML Kit (Text Recognition OCR) |
| **Document Parsing** | Android PdfRenderer |

---

## 📂 Project Structure (Multi-Module)
```text
FitIntel/
├── app/                  → Root navigation and authenticated entry
├── core/
│   ├── data/             → Shared data models and logic
│   ├── navigation/       → App-wide routing definitions
│   └── ui/               → Reusable Compose UI components
└── feature/
    ├── authentication/   → Login and Signup screens
    ├── home/             → Main dashboard UI
    ├── pdf/              → Document picker and ML Kit OCR logic
    ├── gemini/           → AI prompt building and report rendering
    └── maps/             → Live tracking and route plotting
```

---

## 🚦 Navigation Flow

1. **MainActivity**: Checks if the user is logged in. Routes to the **Login** screen or the **Main** dashboard.
2. **Bottom Navigation**: Once logged in, users can switch between **Home**, **Upload PDF**, **Report**, and **Track**.
3. **Data Pipeline**:
   * The **Upload PDF** screen extracts data and saves it.
   * The **Report** screen reads that saved data to ask the AI for insights.
   * The **Track** screen updates the **Home** dashboard with new running stats.