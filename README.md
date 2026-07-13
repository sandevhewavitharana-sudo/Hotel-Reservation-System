# Grandview Hotel Booking System

A hotel reservation management system built with **Java 17 + Spring Boot + MySQL**.
It covers the full booking lifecycle: searching for available rooms, registering
guests, creating bookings with double-booking prevention, confirming / checking in /
checking out, cancellations, and payment tracking — plus a small browser UI on top
of a REST API.

## Why it's built this way

Most beginner hotel-booking projects are a single `Main.java` with `switch`
statements and arrays. This one is instead split into the standard layers you'll
see in real Spring applications, because that separation is the actual thing
worth learning:

```
model/       -> entities (the "nouns": Room, Guest, Booking, Payment)
repository/  -> Spring Data JPA interfaces (talk to the database)
service/     -> business rules (interfaces + *Impl classes)
controller/  -> REST endpoints (JSON API) and web pages (Thymeleaf)
dto/         -> request objects used at the API boundary
exception/   -> custom exceptions + a global handler that turns them into clean error responses
```

Each layer only talks to the one below it. Controllers never touch repositories
directly — they go through a service, and the service is where the actual rules
live (e.g. "you can't check out a booking that hasn't checked in yet").

### OOP concepts you can point to directly

- **Abstraction/Inheritance** — `Person` is an abstract `@MappedSuperclass`;
  `Guest` extends it. If you added `Staff` later, it would extend `Person` too
  and get name/email/phone for free.
- **Polymorphism** — `RoomService`, `BookingService`, `GuestService`, and
  `PaymentService` are interfaces; the controllers depend on the interface
  type, not the concrete `*Impl` class. You could swap `BookingServiceImpl`
  for a different implementation without touching a single controller.
- **Encapsulation** — every entity keeps its fields `private` and exposes
  behavior through methods (`Booking.calculateTotalPrice()`,
  `Room.isBookable()`, `Guest.earnLoyaltyPoints()`) rather than letting
  outside code poke at raw fields.
- **Enums as typed constants** — `RoomType`, `RoomStatus`, `BookingStatus`,
  `PaymentStatus`, `PaymentMethod` replace "magic strings" and let the
  compiler catch typos.

### The one piece of logic worth understanding deeply

`BookingRepository.findOverlappingBookings()` + `RoomServiceImpl.searchAvailableRooms()`
is what actually prevents double bookings. Two date ranges `[checkIn, checkOut)`
overlap unless one ends before the other starts:

```java
booking.checkInDate < requestedCheckOut  AND  requestedCheckIn < booking.checkOutDate
```

If **any** active (non-cancelled) booking on that room matches this condition,
the room is not offered as available, and a direct booking attempt is rejected
with a `409 Conflict`. Walk through this method with your lecturer — it's the
most "real" piece of engineering in the project.

## Project structure

```
hotel-booking-system/
├── pom.xml
├── database/
│   └── schema.sql              (reference schema + sample seed data)
├── src/main/java/com/hotelbooking/
│   ├── HotelBookingApplication.java
│   ├── model/                  (Person, Guest, Room, Booking, Payment, enums)
│   ├── repository/             (Spring Data JPA interfaces)
│   ├── service/ + service/impl/
│   ├── controller/              (REST + web controllers)
│   ├── dto/                     (BookingRequest, GuestRequest)
│   └── exception/               (custom exceptions + global handler)
└── src/main/resources/
    ├── application.properties
    ├── templates/                (Thymeleaf pages: index, book, confirmation, bookings)
    └── static/css/style.css
```

## Setup

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+ running locally

### 2. Create the database
Either let Hibernate create it for you (default), or run the reference schema
yourself:
```bash
mysql -u root -p < database/schema.sql
```

### 3. Configure the connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 4. Run it
```bash
mvn spring-boot:run
```
Then open **http://localhost:8080** for the browser UI, or hit the JSON API
directly at `http://localhost:8080/api/...`.

## Trying it out

**Via the browser:**
1. Go to `/` — pick check-in/check-out dates and search.
2. Click "Book this room" on an available room.
3. You'll need a guest ID — register one first (see API below), or use `1`
   if you loaded the seed data... actually the seed data doesn't include a
   fully wired guest yet, so register one via the API first.
4. Go to `/bookings` to confirm → check in → check out the booking.

**Via the API (curl examples):**

Register a guest:
```bash
curl -X POST http://localhost:8080/api/guests \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Amara","lastName":"Silva","email":"amara@example.com","phone":"0712345678"}'
```

Search available rooms:
```bash
curl "http://localhost:8080/api/rooms/search?checkIn=2026-08-01&checkOut=2026-08-05&roomType=DELUXE"
```

Create a booking:
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"guestId":1,"roomId":5,"checkInDate":"2026-08-01","checkOutDate":"2026-08-05","numberOfGuests":2}'
```

Move it through its lifecycle:
```bash
curl -X POST http://localhost:8080/api/bookings/1/confirm
curl -X POST http://localhost:8080/api/bookings/1/check-in
curl -X POST http://localhost:8080/api/bookings/1/check-out
```

Take a payment:
```bash
curl -X POST "http://localhost:8080/api/payments/bookings/1?method=CREDIT_CARD"
```

## Full API reference

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/guests` | Register a guest |
| GET | `/api/guests` | List guests |
| GET | `/api/guests/{id}` | Get one guest |
| POST | `/api/rooms` | Add a room |
| GET | `/api/rooms` | List rooms |
| GET | `/api/rooms/search?checkIn=&checkOut=&roomType=` | Search availability |
| PATCH | `/api/rooms/{id}/status?status=` | Change room status |
| DELETE | `/api/rooms/{id}` | Remove a room |
| POST | `/api/bookings` | Create a booking |
| GET | `/api/bookings` | List all bookings |
| GET | `/api/bookings/{id}` | Get one booking |
| GET | `/api/bookings/guest/{guestId}` | Bookings for a guest |
| POST | `/api/bookings/{id}/confirm` | PENDING → CONFIRMED |
| POST | `/api/bookings/{id}/check-in` | CONFIRMED → CHECKED_IN |
| POST | `/api/bookings/{id}/check-out` | CHECKED_IN → CHECKED_OUT |
| POST | `/api/bookings/{id}/cancel` | Cancel (allowed unless already checked out/cancelled) |
| POST | `/api/payments/bookings/{bookingId}?method=` | Take payment |
| GET | `/api/payments/bookings/{bookingId}` | Get payment for a booking |
| POST | `/api/payments/bookings/{bookingId}/refund` | Refund |

## Possible extensions (good talking points if asked "what would you add next?")

- Spring Security + login for guests vs. staff/admin roles
- Email confirmation on booking creation
- Nightly rate variation by season/demand
- Pagination on the room/booking list endpoints
- Unit tests for `BookingServiceImpl` (overlap detection, invalid state transitions)
- Docker Compose file for one-command MySQL + app startup

## A note on making this yours

This project is intentionally built with real design decisions behind it —
if your lecturer asks you *why* `RoomService` is an interface with a separate
`RoomServiceImpl`, or *how* the overlap query works, you should be able to
answer from understanding, not memory. Read through `BookingServiceImpl.java`
and the overlap query in `BookingRepository.java` closely before you present —
those two files are where almost all the interesting logic lives, and they're
exactly what a lecturer is likely to poke at first.
