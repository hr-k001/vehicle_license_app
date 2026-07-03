# Vehicle License Portal - Project Workflow

## Tech Stack

| Layer | Technology | Main files |
| --- | --- | --- |
| Frontend | React + Vite + React Router | `frontend/src` |
| Backend | Spring Boot 3.2.5, Java 21 | `src/main/java/com/online` |
| API client | Axios | `frontend/src/api/api.js` |
| Database | MySQL, JPA/Hibernate | `src/main/resources/application.properties`, `repository/*` |
| Test database | H2 | `src/test/resources/application.properties` |

The running app uses MySQL database `vehicle_license_db`. Tests still use H2 so they do not depend on your local MySQL data.

## Main Folders and Files

### Frontend

- `frontend/src/App.jsx`: Defines React routes and role-protected pages.
- `frontend/src/context/AuthContext.jsx`: Stores the logged-in user in `localStorage` as `vlp_user`.
- `frontend/src/api/api.js`: Central Axios API wrapper for all backend calls.
- `frontend/src/pages/Auth.jsx`: Login/register page. Sends `role` to backend so applicant and RTO accounts cannot be mixed.
- `frontend/src/pages/applicant/ApplyLL.jsx`: Learner License application form.
- `frontend/src/pages/applicant/ApplyDL.jsx`: Driving License application form. Checks approved LL before showing the form.
- `frontend/src/pages/applicant/ViewLLStatus.jsx`: Applicant checks LL status by application number.
- `frontend/src/pages/applicant/ViewDLStatus.jsx`: Applicant checks DL status and sees license number after RTO manually generates it.
- `frontend/src/pages/applicant/ScheduleTest.jsx`: Applicant schedules the driving test for a DL application.
- `frontend/src/pages/rto/LLApplications.jsx`: RTO reviews LL applications.
- `frontend/src/pages/rto/DLApplications.jsx`: RTO reviews DL applications and approves only after PASS.
- `frontend/src/pages/rto/ScheduledTests.jsx`: RTO marks driving test PASS/FAIL.
- `frontend/src/pages/rto/ApplicantManagement.jsx`: Manual applicant record management. Mostly for admin corrections, not required for normal application submission.
- `frontend/src/pages/rto/LicenseGeneration.jsx`: RTO manually generates a DL number for an approved DL application.

### Backend

- `VehicleLicenseApplication.java`: Spring Boot entry point.
- `config/AppConfig.java`: Creates service/DAO beans and seeds the default RTO user.
- `config/CorsConfig.java`: Allows frontend requests from Vite.
- `controller/UserController.java`: `/api/users` register/login endpoints.
- `controller/LicenseController.java`: `/api/license` LL/DL apply, status, schedule, generate license endpoints.
- `controller/RTOOfficerController.java`: `/api/rto` approval, rejection, test-result, application-list endpoints.
- `controller/ApplicantController.java`: `/api/applicants` CRUD endpoints for Applicant Management.
- `service/impl/UserServiceImpl.java`: User registration/login service wrapper.
- `service/impl/LicenseServiceImpl.java`: LL/DL application business logic and license number generation.
- `service/impl/RTOOfficerServiceImpl.java`: RTO approval/rejection/test-result rules.
- `service/impl/ApplicantServiceImpl.java`: Applicant CRUD plus duplicate email/Aadhaar checks.
- `dao/impl/UserDaoImpl.java`: Persists users through `UserRepository`; validates password and role.
- `dao/impl/LicenseDaoImpl.java`: Creates LL/DL applications, reuses existing applicants, saves applications to MySQL, and keeps an in-memory cache for existing report/RTO code.
- `dao/impl/RTOOfficerDaoImpl.java`: Reads/writes applications, syncing updates back to MySQL.
- `repository/UserRepository.java`: JPA repository for `users`.
- `repository/ApplicantRepository.java`: JPA repository for `applicants`.
- `repository/ApplicationRepository.java`: JPA repository for `applications`.
- `repository/LicenseRepository.java`: JPA repository for `driving_licenses`.
- `model/User.java`: Login account with `email`, `password`, and `role`.
- `model/Applicant.java`: Applicant personal data, Aadhaar/email uniqueness, DOB validation, vehicle type, and license numbers.
- `model/Application.java`: LL/DL application record, status, payment, test date/result, applicant relation.
- `model/DrivingLicense.java`: Issued DL number record.
- `exception/GlobalExceptionHandler.java`: Converts validation, duplicate, and not-found errors into clean API responses.

## Database Tables

Hibernate creates/updates these tables in MySQL:

- `users`: login accounts. Primary key is `email`. Includes `role` (`applicant` or `rto`).
- `applicants`: personal applicant records. Created automatically from LL/DL application submission or manually from RTO Applicant Management.
- `applications`: LL/DL application records. Primary key is application number such as `LL-1001` or `DL-2001`.
- `driving_licenses`: generated DL numbers after RTO uses Generate License.

## How Applicant Records Are Created

Applicant records are created in two ways:

1. Automatically during LL/DL submission.
   - Applicant fills Apply LL or Apply DL.
   - Frontend sends an `Application` payload containing nested `applicant` details.
   - `LicenseDaoImpl` checks existing applicant by Aadhaar first, then email.
   - If found, it updates/reuses that applicant row.
   - If not found, it creates a new row in `applicants`.
   - The new application row is saved in `applications` and linked to the applicant.

2. Manually in RTO Applicant Management.
   - RTO can create or edit applicant records directly.
   - This is useful for corrections/admin work.
   - It is not required for the normal applicant flow.
   - If the Aadhaar or email already exists, backend returns a clean duplicate message and asks the user to edit the existing record.

Your screenshot error happened because an applicant row already existed from an application submission, then Applicant Management tried to create a second row with the same Aadhaar number. Aadhaar is unique, so MySQL rejected it. The backend now checks this first and returns a readable message.

## Authentication Flow

1. Applicant registration:
   - `Auth.jsx` sends `POST /api/users/register` with `email`, `password`, `role: "applicant"`.
   - `UserDaoImpl` stores it in `users`.

2. RTO login:
   - `AppConfig` seeds `rto@vlp.com` with role `rto`.
   - Public RTO registration is blocked.

3. Login:
   - Frontend sends `email`, `password`, and intended `role`.
   - Backend validates email, password, and role together.
   - Applicant credentials cannot enter RTO pages, and RTO credentials cannot enter applicant pages.
   - Frontend stores the authenticated user in `localStorage`.

## Learner License Workflow

1. Applicant fills `ApplyLL.jsx`.
2. Frontend calls `POST /api/license/ll/apply`.
3. `LicenseController` validates request with `@Valid`.
4. `LicenseServiceImpl.applyForLL` sets type `LL` and date.
5. `LicenseDaoImpl.createLLRequest`:
   - generates `LL-1001`, `LL-1002`, etc.
   - sets status `PENDING`.
   - creates or reuses applicant.
   - saves application to MySQL.
6. RTO opens `LLApplications.jsx`.
7. Frontend calls `GET /api/rto/applications` and filters type `LL`.
8. RTO approves/rejects using:
   - `PUT /api/rto/ll/approve/{appNo}`
   - `PUT /api/rto/ll/reject/{appNo}`
9. Status changes are saved to MySQL.
10. Applicant checks status in `ViewLLStatus.jsx`.

## Driving License Workflow

1. Applicant opens `ApplyDL.jsx`.
2. Frontend checks LL gate with `GET /api/license/ll/check-by-email?email=...`.
3. If no approved LL exists, DL form is blocked.
4. If approved LL exists, applicant submits DL form.
5. Backend creates `DL-2001`, `DL-2002`, etc. with status `PENDING`.
6. Applicant schedules test from `ScheduleTest.jsx`.
7. RTO marks PASS/FAIL in `ScheduledTests.jsx`.
8. RTO can approve DL only if test result is `PASS`.
9. DL approval only changes application status to `APPROVED`; it does not create the license number.
10. RTO manually generates DL number from `LicenseGeneration.jsx`.
11. `LicenseServiceImpl.generateLicenseNumber`:
    - requires approved DL application.
    - generates a unique DL number.
    - stores it on the linked applicant.
    - creates a row in `driving_licenses`.
12. Applicant checks `ViewDLStatus.jsx`.
    - If approved but no license number exists, it shows "DL creation is in progress."
    - After generation, it shows the DL number.

## Data Storage and Retrieval

Normal runtime data flow:

```text
React page
  -> api.js Axios call
  -> Spring controller
  -> service
  -> DAO/repository
  -> MySQL table
```

Application retrieval:

- `LicenseDaoImpl` loads existing applications from MySQL at startup into `applicationStore`.
- New LL/DL submissions are written to both `applicationStore` and `applications`.
- RTO reads all applications through `RTOOfficerDaoImpl.getAllApplications`, which refreshes from `ApplicationRepository`.
- RTO changes are saved back with `ApplicationRepository.save`.
- Reports still read the shared `applicationStore`, which is kept in sync by the DAO layer.

Applicant retrieval:

- Applicant Management uses `ApplicantController` -> `ApplicantServiceImpl` -> `ApplicantRepository`.
- LL/DL submission uses `LicenseDaoImpl` to find applicant by Aadhaar/email and reuse the row.
- This prevents duplicate applicant rows for the same person.

License retrieval:

- Generated DL numbers are stored in `driving_licenses`.
- `ViewLicenseDetails.jsx` fetches by DL number through `LicenseController`.
- `ViewDLStatus.jsx` fetches by application number and displays the generated license number if present on the applicant.

## Important Business Rules

- Applicant must be at least 18 years old for LL/DL submission.
- Phone must be exactly 10 digits.
- Aadhaar must be exactly 12 digits.
- Address must be at least 5 characters.
- Email and Aadhaar are unique in `applicants`.
- Applicant cannot apply for DL unless they have an approved LL.
- DL cannot be approved unless driving test result is `PASS`.
- DL number is generated only from the RTO Generate License page, not automatically during approval.
- Passwords are stored as plain text for this training project. This is not production-safe.

## Main API Endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/users/register` | Applicant registration |
| POST | `/api/users/login` | Applicant/RTO login with role validation |
| POST | `/api/license/ll/apply` | Submit LL application |
| GET | `/api/license/ll/status/{appNo}` | Check LL status |
| GET | `/api/license/ll/check-by-email?email=` | Check approved LL before DL |
| POST | `/api/license/dl/apply` | Submit DL application |
| PUT | `/api/license/dl/{appNo}/schedule-test` | Schedule driving test |
| GET | `/api/license/dl/status/{appNo}` | Check DL status/license generation progress |
| POST | `/api/license/generate/{appNo}` | Generate DL number |
| GET | `/api/license/{licenseNumber}` | View issued license details |
| GET | `/api/rto/applications` | List all applications |
| PUT | `/api/rto/ll/approve/{appNo}` | Approve LL |
| PUT | `/api/rto/ll/reject/{appNo}` | Reject LL |
| PUT | `/api/rto/test/pass/{appNo}` | Mark test PASS |
| PUT | `/api/rto/test/fail/{appNo}` | Mark test FAIL |
| PUT | `/api/rto/dl/approve/{appNo}` | Approve DL after PASS |
| PUT | `/api/rto/dl/reject/{appNo}` | Reject DL |
| GET | `/api/applicants` | List applicant records |
| POST | `/api/applicants` | Manually create applicant |
| PUT | `/api/applicants/{id}` | Update applicant |
| DELETE | `/api/applicants/{id}` | Delete applicant |
