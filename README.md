# RAF Food Ordering System

## Project Overview
The **RAF Food Ordering System** is a web application that simulates food ordering. Users can place and track orders (e.g., **ORDERED → PREPARING → IN_DELIVERY → DELIVERED**), cancel when allowed, and schedule future orders. The project covers **user management**, **order management**, **permissions/authorization**, **background processing**, **real-time status updates**, and **error logging** for scheduled jobs.

---

## Key Features

### Orders & Lifecycle
- **Create/Cancel/Track/Schedule** orders.
- Automatic status transitions in the background:
  - `ORDERED` → `PREPARING` (after ~10s, with jitter)  
  - `PREPARING` → `IN_DELIVERY` (after ~15s)  
  - `IN_DELIVERY` → `DELIVERED` (after ~20s)
- While an order is in **PREPARING/IN_DELIVERY/DELIVERED**, other operations on the same order are blocked.

### Permissions & Roles
- Action-scoped permissions:  
  `can_search_order`, `can_place_order`, `can_cancel_order`, `can_track_order`, `can_schedule_order`.
- **Regular users** see only their orders; **admins** can see all users’ orders.

### Scheduling & Error Logging
- Schedule orders for a future date/time.
- If system limits prevent execution, log an entry to **ErrorMessage** (`date`, `orderId`, `operation`, `message`).
- Dedicated **Errors** page (with pagination) for admins; regular users see only their own errors.

### Concurrency Limit
- At most **3 simultaneous orders** in **PREPARING/IN_DELIVERY**.  
  New (or scheduled) orders exceeding this limit are rejected and logged to **ErrorMessage**.

### Actions (API/Use Cases)
- **SEARCH** – filter by `status[]`, `dateFrom–dateTo`, and (admin) `userId`.
- **PLACE_ORDER** – instant creation (`ORDERED`).
- **CANCEL** – only from `ORDERED`.
- **TRACK** – get current status (frontend auto-refresh via WebSocket/SSE/polling).
- **SCHEDULE** – create at a future time; background executor processes the job.

---

## Data Model (core)
**Order**
- `id: Long` – unique ID  
- `status: Enum(ORDERED, PREPARING, IN_DELIVERY, DELIVERED, CANCELED)`  
- `createdBy: FK_User(id)` – owner  
- `active: Boolean` – active/canceled flag  
- `items: List<Dish>` – ordered dishes (Dish catalog is implementation-defined)

**ErrorMessage**
- `date`, `orderId`, `operation`, `message`

> *User management is reused from the earlier assignment, extended with order permissions.*

---

## Frontend Pages
- **Orders Search** – lists orders; filter form (status, date range, userId for admins). Auto-refresh statuses.
- **Create Order** – form to select dishes and place an order.
- **Errors History** – paginated table of error logs (scoped to user; admins see all).

---

## Technology Stack
- **Backend:** Spring or JBoss (REST), Relational DB (transactions, constraints, indexes)
- **Frontend:** Angular 2+ / Vue / React
- **Auth:** Role + permission checks (per action)
- **Realtime:** WebSocket / Server-Sent Events / polling (for status updates)

---

## Architecture & Behavior
- All endpoints return **2xx immediately**; long-running transitions execute in background workers.
- Background executors enforce lifecycle timing and **concurrency limit (≤ 3)**.
- Robust validation and friendly error messages (no stack traces shown to users).
- Pagination implemented on **both** frontend and backend.

---

## Learning Outcomes
- Implementing **RBAC with fine-grained permissions**.
- Designing **asynchronous workflows** with background transitions and real-time UI updates.
- Enforcing **domain constraints** (state machine, concurrency limits).
- Building **search & scheduling** features with resilient error logging.
- Full-stack integration: REST API ↔ DB ↔ modern SPA frontend.

---

🚀 Course: **NWP – Web Applications** (RAF). Deadline: **Sept 3, 2025** (with earlier defenses allowed).
