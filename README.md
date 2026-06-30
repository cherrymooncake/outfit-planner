# Outfit Planner

A cross-platform system designed for digital wardrobe management and outfit planning. The project features an interactive 2D styling canvas, automated image processing, and context-aware AI recommendations.

## Tech Stack

**Backend**
* C#, ASP.NET Core 9.0 (REST API)
* Entity Framework Core
* PostgreSQL 17
* JWT Authentication & Bcrypt password hashing

**Android Client**
* Kotlin, Jetpack Compose
* Architecture: Clean Architecture, MVI (Model-View-Intent)
* Local Data: Room (Offline-first approach)
* Networking: Retrofit 2, Coil for image loading

**Web Client**
* Vue 3 (Composition API), TypeScript
* State Management: Pinia
* UI Framework: Element Plus
* Canvas Manipulation: Fabric.js

**AI & Computer Vision Microservices**
* Python 3.13, FastAPI
* Computer Vision: OpenCV, rembg (U^2-Net model) for background removal
* NLP: sentence-transformers (semantic search for outfit suggestions)

## Key Features

* **Interactive Styling Canvas:** visual 2D editor allows users to scale, rotate, and layer (Z-index) clothing items. Supports reusable layout templates.
* **Intelligent Planning:** a calendar-based system for scheduling outfits. Includes automated random generation and manual daily assignment.
* **Automated Image Processing:** AI-powered background removal upon upload. Includes a manual mask editor with Lasso and Eraser tools for precision.
* **AI Stylist:** semantic recommendation engine that suggests outfits by analyzing natural language prompts and real-time weather data (via Open-Meteo API).
* **System Administration:** dedicated dashboard for monitoring service health, user statistics, and managing role-based access control.
* **Data Security:** asynchronous database and file backup generation (SQL dumps and media archives).
