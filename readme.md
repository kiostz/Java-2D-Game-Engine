# 2D Java RPG Game Engine

> A robust, high-performance 2D game engine built from scratch using Java (Swing/AWT).
<img width="767" height="392" alt="titile" src="https://github.com/user-attachments/assets/cf9fea8b-b65d-4779-adae-bb27a463e312" />

## 📖 Overview

**2D Java RPG Game Engine** is a custom-built game engine designed to demonstrate core concepts of game development, computer graphics, and software architecture. Unlike projects using pre-made engines like Unity or Godot, this project implements the fundamental systems—rendering, physics, collision, and state management—purely in Java.

This project serves as a showcase of Object-Oriented Programming (OOP), algorithmic problem solving (Y-Sorting, AABB Collision), and system design patterns.

## 🏛️ Architecture Highlights

### Data-Driven Design
*   **JSON Object Management**: Game objects (items, doors, chests) are defined in an external `objects.json` file. This allows for adding or modifying hundreds of objects without recompiling the source code.
*   **Object Factory**: A centralized `ObjectFactory` reads the JSON data at runtime and dynamically constructs game objects, decoupling object data from game logic.

### Decoupled Rendering
*   **WorldRenderer**: All world rendering is handled by a dedicated `WorldRenderer` class. This separates drawing logic from game logic, adhering to the Single Responsibility Principle.
*   **Data Pull Model**: The renderer pulls data directly from entities (`position`, `animation state`, etc.) rather than entities pushing data to the renderer. This is a common pattern in modern game engines.

### Component-Based Animation
*   **Animation Component**: A reusable `Animation` class encapsulates all logic for sprite sheet handling, frame timing, and looping.
*   **Stateful Animation**: Entities now manage animation *states* (e.g., `currentAnim = walkDown`), while the `Animation` component handles the frame-by-frame updates, cleaning up the entity classes significantly.

### Modular Systems
*   **Font Manager**: A `FontManager` handles the loading and provision of custom `.ttf` fonts, allowing for easy UI and localization changes.
*   **Sound Manager**: A `Sound` class abstracts away the complexities of `javax.sound`, providing simple `play()`, `stop()`, and `loop()` methods for game-wide audio.

## ✨ Key Features

### 🛠 Core Engine
*   **Custom Game Loop**: Implements a 'Delta Time' accumulator loop to decouple game logic updates from rendering, ensuring consistent physics simulation regardless of frame rate.
*   **State Management**: Robust state machine handling multiple game phases: `TitleState`, `PlayState`, `PauseState`, and `DialogueState`.

### 🎨 Rendering Pipeline
*   **Y-Sorting (Depth Buffering)**: Implements the Painter's Algorithm to dynamically sort entities by their Y-coordinate every frame, ensuring correct visual layering (e.g., walking behind a tree vs. in front of it).
*   **Dynamic Camera**: Viewport calculations that center the player and cull off-screen entities for performance optimization.

### 🎮 Gameplay & Physics
*   **Vector-Based Movement**: Implements normalized movement vectors to solve the "diagonal speedup" problem, ensuring consistent velocity in 8 directions.
*   **Collision Detection**: AABB (Axis-Aligned Bounding Box) collision system checking interactions between dynamic entities (Player/NPCs) and static tilemaps.
*   **Interaction System**: Proximity-based interaction logic allowing the player to talk to NPCs, open chests, and unlock doors.

### 💬 UI & Dialogue
*   **Floating Chat Bubbles**: An entity-anchored dialogue system that supports dynamic text centering, alpha-blended fade-out effects, and stacking messages.
*   **HUD**: Real-time display of player stats and play time.

## 📸 Screenshots

| Exploration | Dialogue System |
|:-----------:|:-------------:|
|<img width="765" height="573" alt="sort" src="https://github.com/user-attachments/assets/77078aac-9117-4077-8a98-49578c196980" />|<img width="765" height="571" alt="dialogue" src="https://github.com/user-attachments/assets/31540e87-6865-4835-ae79-9d69dede184a" />|
| *Y-Sorting and Tile Rendering* | *Floating Chat Bubbles with Fade* |

| Debug Mode | Collisions |
|:----------:|:-----:|
|<img width="768" height="574" alt="debug" src="https://github.com/user-attachments/assets/a948a87f-389d-4adc-9417-fa77a45da0f8" />|<img width="764" height="576" alt="explore" src="https://github.com/user-attachments/assets/20966da6-a3b8-4d98-88d6-6af79185d374" />|
| *Hitbox and Interaction Areas* | *NPC Collisions* |

## 🚀 Getting Started

### Prerequisites
*   **Java Development Kit (JDK)**: Version 8 or higher (Java 17 recommended).
*   **IDE**: IntelliJ IDEA (Recommended) or Eclipse.

### Installation
1.  **Clone the repository**:
    ```sh
    git clone https://github.com/kiostz/Java-2D-Game-Engine.git
    ```
2.  **Open the project** in your preferred IDE (e.g., IntelliJ IDEA).
3.  Mark the `res` folder as Sources Root. Right click `res` > Mark Directory as > Sources Root
5.  **Run the `Main.java`** file located in `src/main/`.

### Controls
*   **Movement**: W, A, S, D
*   **Interact**: Enter / E
*   **Toggle Debug**: T
*   **Pause**: Escape

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
