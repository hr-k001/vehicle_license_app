![alt text](image.png)# Vehicle License Portal — Project Workflow

## Tech Stack at a Glance

| Layer      | Technology                         | Port  |
|------------|------------------------------------|-------|
| Frontend   | React 18 + Vite + React Router v6  | 5173  |
| Backend    | Java 21, Spring Boot 3.2.5, Maven  | 8080  |
| Storage    | In-memory (`HashMap`) — no database|  —    |

---

## System Architecture

```
Browser (React)
     │
     │  HTTP REST  (axios, base: http://localhost:8080/api)
     ▼
Spring Boot Controllers  (/api/users, /api/license, /api/rto)
     │
     ▼
Service Layer  (business logic, validations)
     │
     ▼
DAO Layer  (reads/writes to in-memory HashMaps)
     │
     ▼
Two in-memory stores (live only while the server is running)
  ┌─────────────────────────────┐   ┌────────────────────────────────┐
  │  userStore                  │   │  applicationStore               │
  │  Map<String, User>          │   │  Map<String, Application>       │
  │  key = email                │   │  key = applicationNumber        │
  └─────────────────────────────┘   └────────────────────────────────┘
```

> **Important:** Because there is no database, all data is lost when the Spring Boot server restarts.

---

## Where Data Is Stored

### 1. `userStore` — inside `UserDaoImpl`
- Type: `HashMap<String, User>`
- Key: user's email address
- Value: `User` object (`email`, `password`)
- Created when: user calls **Register**
- Used when: user calls **Login** (password compared directly — no hashing)

### 2. `applicationStore` — inside `LicenseDaoImpl`
- Type: `HashMap<String, Application>`
- Key: application number (e.g. `LL-1001`, `DL-2001`)
- Value: `Application` object (see fields below)
- **Shared** with `RTOOfficerDaoImpl` via `AppConfig` — both DAOs point to the **same** HashMap instance, so when an applicant submits and an RTO officer approves, they are reading/writing the same object in memory

### Application Object Fields
| Field             | Type              | Set by                          |
|-------------------|-------------------|---------------------------------|
| `applicationNumber` | String          | Auto-generated (LL-1001+, DL-2001+) |
| `type`            | `LL` or `DL`      | Backend on create               |
| `status`          | `PENDING` → `APPROVED` / `REJECTED` | RTO officer |
| `applicant`       | `Applicant` object | Submitted by user in form      |
| `testDate`        | Date              | Set by applicant (Schedule Test)|
| `testResult`      | `PASS` / `FAIL` / null | Set by RTO officer         |
| `amountPaid`      | double            | Hardcoded (LL ₹200, DL ₹500)   |
| `paymentStatus`   | String            | Hardcoded `PAID` on submit      |
| `modeOfPayment`   | String            | Hardcoded `Online`              |

---

## Authentication — How It Works

There is **no JWT or session token**. The backend simply returns `"Login successful"` or `"Invalid credentials"`.  
The frontend stores the logged-in user in **localStorage** under the key `vlp_user`.

```
User enters email + role on login form
       │
       ▼
POST /api/users/login  { email, password }
       │
Backend checks userStore → returns "Login successful" or error
       │
       ▼
Frontend stores in localStorage:
  { email: "him@nav.com", role: "applicant" }   ← or "rto"
       │
       ▼
React AuthContext reads localStorage on every page load
Role-based routing: applicant → /app/dashboard
                    rto       → /app/rto-dashboard
```

**RTO accounts** are pre-registered manually (same `/register` endpoint, role passed as a field).  
The frontend's register form hard-codes `role: "applicant"` for new sign-ups.

---

## Complete Application Workflow (Step by Step)

### Step 1 — Register & Login
```
Applicant                      Backend (UserController)
    │                                │
    │── POST /api/users/register ───►│  stores {email, password} in userStore
    │◄─ "User account created" ──────│
    │
    │── POST /api/users/login ──────►│  looks up email in userStore, checks password
    │◄─ "Login successful" ──────────│
    │
Frontend saves to localStorage: { email, role: "applicant" }
```

---

### Step 2 — Apply for Learner License (LL)
```
Applicant fills form → POST /api/license/ll/apply
   payload: { applicant: {...}, modeOfPayment, amountPaid, paymentStatus }
                │
                ▼
LicenseController → LicenseServiceImpl → LicenseDaoImpl
   - generates applicationNumber = "LL-" + (1000 + n)
   - sets status = PENDING
   - puts into applicationStore["LL-1001"] = application
                │
                ▼
Returns: { applicationNumber: "LL-1001", status: "PENDING", ... }
Applicant saves the application number to check status later.
```

---

### Step 3 — RTO Reviews LL Application
```
RTO Officer opens "LL Applications" page
   │
   ├── GET /api/rto/applications
   │     RTOOfficerDaoImpl reads the same applicationStore
   │     returns all Application objects as a list
   │     Frontend filters type === "LL"
   │
   ├── RTO clicks a row → sees applicant details
   │
   ├── Clicks APPROVE → PUT /api/rto/ll/approve/LL-1001
   │     Sets application.status = APPROVED
   │     Saves back to applicationStore
   │
   └── Clicks REJECT → PUT /api/rto/ll/reject/LL-1001
         Sets application.status = REJECTED
```

---

### Step 4 — Check LL Status (Applicant)
```
Applicant enters their application number
   │
   ├── GET /api/license/ll/status/LL-1001
   │     LicenseDaoImpl.getApplicationById("LL-1001")
   │     Returns the Application object (with current status)
   │
   └── Frontend displays: PENDING / APPROVED / REJECTED
```

---

### Step 5 — Apply for DL (Gate: LL must be APPROVED)
```
Applicant opens "Apply for DL" page
   │
   ├── GET /api/license/ll/check-by-email?email=him@nav.com
   │     LicenseDaoImpl searches applicationStore for:
   │       type == LL  AND  applicant.email == him@nav.com
   │     Prefers the APPROVED one if multiple exist
   │     Returns: { status: "APPROVED", applicationNumber: "LL-1001" }
   │
   ├── If status != APPROVED → frontend shows a RED BLOCK BANNER
   │     "You cannot apply for DL yet" (with reason)
   │     Form is not shown at all.
   │
   └── If status == APPROVED → form is shown
         POST /api/license/dl/apply
           - generates applicationNumber = "DL-" + (2000 + n)
           - sets status = PENDING, type = DL
           - stores in applicationStore["DL-2001"]
```

---

### Step 6 — Schedule Driving Test (Applicant)
```
Applicant enters DL application number + chooses a date/time
   │
   └── PUT /api/license/dl/DL-2001/schedule-test
         body: { testDate: "2026-07-10T10:00:00" }
         LicenseDaoImpl sets application.testDate = provided date
         Saves back to applicationStore
```

---

### Step 7 — RTO Marks Test Result
```
RTO Officer opens "Scheduled Tests" page
   │
   ├── GET /api/rto/applications
   │     Frontend filters: type === "DL" AND testDate != null
   │
   ├── Clicks PASS → PUT /api/rto/test/pass/DL-2001
   │     RTOOfficerDaoImpl sets application.testResult = "PASS"
   │
   └── Clicks FAIL → PUT /api/rto/test/fail/DL-2001
         RTOOfficerDaoImpl sets application.testResult = "FAIL"
```

---

### Step 8 — RTO Approves / Rejects DL
```
RTO Officer opens "DL Applications" page
   │
   ├── Clicks a row → sees applicant details + test date + test result
   │
   ├── APPROVE button is DISABLED if testResult != "PASS"
   │     (enforced on both frontend and backend)
   │
   ├── Clicks APPROVE → PUT /api/rto/dl/approve/DL-2001
   │     Backend checks: if testResult != "PASS" → returns error
   │     Otherwise: sets application.status = APPROVED
   │
   └── Clicks REJECT → PUT /api/rto/dl/reject/DL-2001
         Sets application.status = REJECTED (no test requirement)
```

---

### Step 9 — Check DL Status (Applicant)
```
Applicant enters DL application number
   └── GET /api/license/dl/status/DL-2001
         Returns Application object with current status + testResult
         Frontend shows status card with all details
```

---

## Shared Store — Why It Matters

```
AppConfig.java (Spring @Configuration)

  LicenseDaoImpl licenseDaoImpl = new LicenseDaoImpl();
       │
       │  licenseDaoImpl.getApplicationStore() ← returns the HashMap reference
       ▼
  RTOOfficerDaoImpl rtoDao = new RTOOfficerDaoImpl( ← receives the SAME HashMap )

Result:
  Applicant submits LL via LicenseDaoImpl    → writes to applicationStore["LL-1001"]
  RTO reads applications via RTOOfficerDaoImpl → reads the SAME applicationStore["LL-1001"]
  RTO approves via RTOOfficerDaoImpl          → mutates the SAME object in memory
  Applicant checks status via LicenseDaoImpl  → reads the updated object
```

Without this sharing, the RTO and applicant would be looking at two separate, disconnected stores.

---

## URL / Route Map

### Backend REST Endpoints
| Method | URL                                         | What it does                            |
|--------|---------------------------------------------|-----------------------------------------|
| POST   | `/api/users/register`                       | Register a new user                     |
| POST   | `/api/users/login`                          | Login                                   |
| POST   | `/api/license/ll/apply`                     | Submit LL application                   |
| GET    | `/api/license/ll/status/{appNo}`            | Check LL status                         |
| GET    | `/api/license/ll/check-by-email?email=`     | Gate check before DL application        |
| POST   | `/api/license/dl/apply`                     | Submit DL application                   |
| PUT    | `/api/license/dl/{appNo}/schedule-test`     | Book driving test slot                  |
| GET    | `/api/license/dl/status/{appNo}`            | Check DL status                         |
| PUT    | `/api/rto/ll/approve/{appNo}`               | RTO: approve LL                         |
| PUT    | `/api/rto/ll/reject/{appNo}`                | RTO: reject LL                          |
| PUT    | `/api/rto/dl/approve/{appNo}`               | RTO: approve DL (requires PASS)         |
| PUT    | `/api/rto/dl/reject/{appNo}`                | RTO: reject DL                          |
| GET    | `/api/rto/applications`                     | List all applications                   |
| GET    | `/api/rto/applications/search?q=`           | Search applications                     |
| PUT    | `/api/rto/test/pass/{appNo}`                | RTO: mark driving test PASS             |
| PUT    | `/api/rto/test/fail/{appNo}`                | RTO: mark driving test FAIL             |

### Frontend Routes
| Path                            | Role      | Page                              |
|---------------------------------|-----------|-----------------------------------|
| `/`                             | Anyone    | Landing page                      |
| `/auth`                         | Anyone    | Login / Register                  |
| `/app/dashboard`                | Applicant | Dashboard with action cards       |
| `/app/apply-ll`                 | Applicant | Apply for Learner License         |
| `/app/ll-status`                | Applicant | Check LL status                   |
| `/app/apply-dl`                 | Applicant | Apply for DL (blocked if no LL)   |
| `/app/schedule-test`            | Applicant | Schedule driving test             |
| `/app/dl-status`                | Applicant | Check DL status                   |
| `/app/rto-dashboard`            | RTO       | RTO dashboard with stats          |
| `/app/rto/ll-applications`      | RTO       | Review all LL applications        |
| `/app/rto/dl-applications`      | RTO       | Review all DL applications        |
| `/app/rto/scheduled-tests`      | RTO       | Mark driving test PASS / FAIL     |
| `/app/rto/applications`         | RTO       | All applications (read-only view) |
| `/app/rto/search`               | RTO       | Search by name / email / app no   |

---

## Key Business Rules

1. **DL application is blocked** unless the applicant has an `APPROVED` LL — enforced at both frontend (block banner) and backend (`checkLLByEmail` endpoint).
2. **DL cannot be approved** unless `testResult == "PASS"` — enforced at both frontend (Approve button is disabled) and backend (`RTOOfficerServiceImpl.approveDrivingLicense` returns an error if not PASS).
3. **Test result is final** — once PASS or FAIL is set, the button disappears from the Scheduled Tests page (no override UI).
4. **Passwords are stored in plain text** — no hashing. Acceptable for this academic project, not for production.
5. **No JWT / session management** — role is stored in localStorage; anyone can edit it in DevTools. Again, acceptable for academic scope.
