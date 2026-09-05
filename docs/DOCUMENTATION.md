# Driving School Management System

**Project documentation - Assignment 3, Project Phase 1**

Course: Web Technologies
Institution: Adventist University of Central Africa
Student: _your name_
Student number: _your number_
Date: _submission date_

---

> **Before you submit:** this document is a working draft. Read every section,
> change anything that does not match how you would describe the project, and
> replace every placeholder in italics. A lecturer can tell the difference
> between a document its author has read and one they have not.

---

## 1. Abstract

Driving schools in Rwanda coordinate three scarce resources for every practical
lesson: a student, a qualified instructor and a training vehicle. Most schools
manage this with a paper diary, which cannot enforce the one rule that matters
most - that no instructor and no vehicle may be committed to two overlapping
lessons.

This project delivers a web-based Driving School Management System built with
JavaServer Faces and Hibernate over a PostgreSQL database. It manages student
enrolment and lesson scheduling, and refuses any booking that would double-book
a student, an instructor or a vehicle. The clash is detected by a database
query executed before the booking is accepted, so the conflict surfaces at the
moment of booking rather than at the kerb on the day of the lesson.

The system implements complete create, read, update and delete operations on
the Student and Lesson entities, applies all three categories of JSF validation,
and separates concerns across a model, a data access and a presentation layer.

---

## 2. Problem statement

A driving school running a paper diary faces four recurring problems.

**Double booking.** A lesson consumes three resources at once. A clerk writing
a booking has to scan the diary by eye for every clash across all three. The
scan is unreliable, and the failure only becomes visible when two students
arrive expecting the same instructor or the same car. One of them is sent home,
and the school loses both the revenue and the goodwill.

**No enforcement of enrolment rules.** A student under the legal minimum age of
18 can be enrolled and given lessons before anyone notices the date of birth on
the paper form. The error is expensive because the student cannot sit the
national test regardless of how many lessons they have paid for.

**No reliable record.** Diary entries are amended, crossed out or lost.
Questions such as how many lessons a particular student has completed, or how
heavily a particular vehicle is used, cannot be answered without leafing
through months of pages.

**No concurrent access.** Only one person can hold the diary. A second clerk
taking a phone booking cannot see what the first has just written down, which
is itself a cause of double booking.

The underlying issue is that a paper diary stores data but enforces no rules.
Every constraint that matters lives in the memory and attention of the clerk.

---

## 3. Scope of the project

### In scope for Phase 1

- Student enrolment with full create, read, update and delete
- Lesson scheduling with full create, read, update and delete
- Registration of instructors and vehicles as supporting data
- Automatic rejection of any booking that overlaps an existing commitment for
  the same student, instructor or vehicle
- Enforcement of the minimum enrolment age of 18
- Persistent storage of all records in PostgreSQL
- A browser-based interface usable by school office staff

### Out of scope for Phase 1

- User accounts, log in and role-based access control
- Fee payment processing and receipt printing, although the Payment entity is
  modelled and mapped
- Theory mock examinations, although the MockExam entity is modelled and mapped
- Notifications to students by SMS or email
- Reporting and analytics dashboards
- Instructor availability calendars and working-hours rules
- Integration with the national licensing authority

### Assumptions

- Lessons are booked on a single date and do not run past midnight
- A cancelled lesson immediately releases its instructor and its vehicle
- Office staff are trusted users; the system does not need to defend against
  malicious internal use in this phase
- One physical branch, so no multi-site resource pooling is required

---

## 4. AS-IS model

The current, manual process.

1. A prospective student walks into the office and fills in a paper enrolment
   form with their name, telephone number, date of birth and the licence
   category they want.
2. The clerk files the form in a folder. Nobody systematically checks the date
   of birth against the legal minimum age.
3. When the student asks for a lesson, the clerk opens the diary at the
   requested date and looks down the page for a free slot.
4. The clerk scans by eye for whether the intended instructor already has a
   booking that overlaps, then repeats the scan for the intended vehicle.
5. If the slot looks free, the clerk writes the student's name, the
   instructor's initials and the vehicle plate into the page.
6. On the day, the instructor consults the diary to see who they are teaching.
7. Attendance and lesson notes are written into the margin, if at all.

### Weaknesses of the AS-IS process

| Step | Weakness | Consequence |
|---|---|---|
| 2 | Age is never verified | Under-age students are enrolled and paid for lessons they cannot use |
| 4 | Clash detection is a manual visual scan | Double bookings are frequent and are only discovered on the day |
| 5 | One physical diary | Two clerks cannot book at the same time |
| 5 | Entries are amended in pen | No reliable history of what changed and when |
| 7 | Records live in margins | No way to answer questions about lesson history or vehicle utilisation |

---

## 5. TO-BE model

The proposed, system-supported process.

1. The clerk opens the student enrolment page and enters the student's details.
   The system rejects the enrolment if any required field is missing, if the
   telephone number is not a valid Rwandan mobile number, or if the date of
   birth gives an age under 18. A valid enrolment is written to the database
   with an audit timestamp.
2. The clerk opens the lesson scheduling page and selects a student, an
   instructor, a vehicle, a date and a time slot from drop-down lists populated
   from the database.
3. On submission the system checks that the end time is later than the start
   time, then queries every existing lesson on that date for an overlap
   involving the same student, the same instructor or the same vehicle.
4. If a clash exists the booking is refused, and the message names which
   resource is already committed and to what. Nothing is written to the
   database.
5. If no clash exists the lesson is saved and immediately appears in the
   schedule, visible to any other clerk at any other workstation.
6. Lessons can be edited or cancelled. A cancelled lesson releases its
   instructor and vehicle, and stops blocking new bookings.
7. The full schedule is queryable at any time from the lesson list.

### Comparison

| Concern | AS-IS | TO-BE |
|---|---|---|
| Clash detection | Manual visual scan | Database query, automatic, before commit |
| Age checking | Not performed | Enforced by a custom validator |
| Concurrent access | One diary, one clerk | Any number of workstations |
| Data durability | Paper, amendable in pen | PostgreSQL, with created and updated timestamps |
| Cancellation handling | Crossed out by hand | Status change that releases resources |
| Auditability | None | Every row carries created and updated timestamps |

---

## 6. Business requirements

### Functional requirements

| ID | Requirement | Priority |
|---|---|---|
| FR-01 | The system shall allow office staff to enrol a student, capturing ID, name, telephone, email, date of birth, licence category and status | Must |
| FR-02 | The system shall reject the enrolment of any student under 18 years of age | Must |
| FR-03 | The system shall reject a student ID that is already in use | Must |
| FR-04 | The system shall allow the details of an enrolled student to be viewed, updated and deleted | Must |
| FR-05 | The system shall allow instructors to be registered with their licence category | Must |
| FR-06 | The system shall allow training vehicles to be registered by number plate | Must |
| FR-07 | The system shall allow a lesson to be scheduled by selecting a student, an instructor, a vehicle, a date and a time slot | Must |
| FR-08 | The system shall reject any lesson whose end time is not later than its start time | Must |
| FR-09 | The system shall reject any lesson that overlaps an existing non-cancelled lesson involving the same student, instructor or vehicle | Must |
| FR-10 | The system shall state which resource caused a rejected booking to clash | Should |
| FR-11 | The system shall allow a scheduled lesson to be viewed, rescheduled and deleted | Must |
| FR-12 | The system shall treat back-to-back lessons as non-overlapping | Must |
| FR-13 | The system shall not allow a cancelled lesson to block a new booking | Must |
| FR-14 | The system shall record a creation and a last-updated timestamp on every record | Should |
| FR-15 | The system shall display a confirmation message on every successful operation | Should |

### Non-functional requirements

| ID | Requirement |
|---|---|
| NFR-01 | Any page shall respond within two seconds for a database of up to 5,000 lessons |
| NFR-02 | The system shall run on any modern browser without a plug-in |
| NFR-03 | The system shall use parameterised queries throughout, so that no user input is concatenated into SQL |
| NFR-04 | The system shall be deployable as a single WAR to Apache Tomcat 9 |
| NFR-05 | Database credentials shall be held in one configuration file, not scattered through the code |
| NFR-06 | A validation failure shall never leave a partially written record in the database |

---

## 7. Software qualities applied

### Correctness

The scheduling rule is stated precisely and implemented to match. Two lessons
overlap when `existing.startTime < new.endTime AND existing.endTime > new.startTime`.
The inequalities are strict, so a lesson ending at 10:00 and one starting at
10:00 are back-to-back rather than clashing. When an existing lesson is being
edited, its own row is excluded from the comparison so that it does not report
a clash with itself.

### Reliability

Every write runs inside a Hibernate transaction. If any statement fails, the
catch block rolls the transaction back, so a failure can never leave a
half-written record. Deleting a student removes its dependent lesson, payment
and mock exam rows in the same transaction, so a foreign key violation cannot
leave orphaned data behind.

### Maintainability

The code is separated into four layers, each with one responsibility:

- `model` holds the entities and the mapping annotations
- `dao` holds all database access; no SQL or HQL appears anywhere else
- `bean` holds presentation logic and business rules
- `util` and `validator` hold reusable conversion and validation

Because each layer only talks to the one below it, a change to the database
schema is confined to `model` and `dao`, and a change to the page layout
touches only the XHTML.

### Reusability

`Audit` is a `@MappedSuperclass`, so all six entities inherit their timestamp
columns and the lifecycle callbacks that populate them, from one definition.
`LegalAgeValidator` is registered under an id and can be attached to any date
input on any page. The converters are written once and used by every form that
handles a date, a time or an entity drop-down.

### Usability

Required fields are marked. Every error message names the field and states what
was expected, rather than reporting a generic failure. Format hints appear
under the inputs that need them. Lesson status is colour-coded. A double
booking message names the specific instructor, vehicle or student that is
already committed, and the slot they are committed to, so the clerk can offer
an alternative immediately.

### Performance

`HibernateUtil` builds exactly one `SessionFactory` for the lifetime of the
application. Building one per request would exhaust the connection pool.

The lesson queries use `JOIN FETCH` to load each lesson together with its
student, instructor and vehicle in a single statement. Without it, Hibernate
would issue one extra query per association per row - the N+1 problem - so
twenty lessons would cost sixty-one queries instead of one.

### Security

All HQL uses named parameters, never string concatenation of user input, so the
queries cannot be manipulated by what a user types. Server-side validation is
authoritative; nothing relies on the browser to enforce a rule.

_Known limitation for Phase 2: database credentials are currently stored in
plain text in `hibernate.cfg.xml`, and the application has no authentication.
Both are acceptable for a development build and neither would be acceptable in
production._

### Portability

Database-specific SQL is never written by hand. Hibernate generates it from the
configured dialect, so moving from PostgreSQL to another database is a
configuration change rather than a rewrite.

---

## 8. Initial class diagram

### Entities and attributes

**Audit** (`@MappedSuperclass`, no table of its own)
`createdAt: LocalDateTime`, `updatedAt: LocalDateTime`

**Student** (extends Audit)
`studentId: String (PK)`, `firstName: String`, `lastName: String`,
`phone: String`, `email: String`, `dateOfBirth: LocalDate`,
`licenseCategory: String`, `enrollmentDate: LocalDate`, `status: String`

**Instructor** (extends Audit)
`instructorId: String (PK)`, `firstName: String`, `lastName: String`,
`phone: String`, `licenseCategory: String`, `hireDate: LocalDate`

**Vehicle** (extends Audit)
`plateNumber: String (PK)`, `make: String`, `model: String`,
`transmission: String`, `category: String`, `available: boolean`

**Lesson** (extends Audit)
`lessonId: Long (PK, generated)`, `student: Student (FK)`,
`instructor: Instructor (FK)`, `vehicle: Vehicle (FK)`,
`lessonDate: LocalDate`, `startTime: LocalTime`, `endTime: LocalTime`,
`status: String`, `notes: String`

**Payment** (extends Audit)
`paymentId: Long (PK, generated)`, `student: Student (FK)`, `amount: Double`,
`paymentDate: LocalDate`, `method: String`, `reference: String`

**MockExam** (extends Audit)
`examId: Long (PK, generated)`, `student: Student (FK)`,
`examDate: LocalDate`, `score: Double`, `passed: boolean`

### Relationships

| From | To | Cardinality | Meaning |
|---|---|---|---|
| Student | Lesson | 1 to many | a student attends many lessons |
| Instructor | Lesson | 1 to many | an instructor teaches many lessons |
| Vehicle | Lesson | 1 to many | a vehicle is used in many lessons |
| Student | Payment | 1 to many | a student makes many fee payments |
| Student | MockExam | 1 to many | a student sits many mock exams |
| Audit | all six | generalisation | every entity inherits audit timestamps |

`Lesson` is the associative entity at the centre of the model. It holds three
foreign keys, which is precisely why a double booking is structurally possible
and why the overlap rule has to be enforced in code.

> **Insert your class diagram image here.** Draw it in draw.io, Lucidchart or
> StarUML from the tables above, export it as PNG, and place it in this
> document. A diagram drawn by hand from your own model is better than a
> generated one you cannot explain.

---

## 9. Implementation summary

### The three types of validation

| Type | Where | What it checks | Why it must be this type |
|---|---|---|---|
| Built-in | `students.xhtml`, `setup.xhtml` | required fields, ID length, phone and plate patterns | Simple single-field format rules, expressible declaratively |
| Custom | `LegalAgeValidator` | student is at least 18, date of birth is not in the future | A calculation across two dates that no built-in tag can express, needed on more than one form |
| Application level | `LessonBean.save()` | end time after start time; no double booking; student ID not already taken | One rule compares two fields, the others query other rows; a validator sees only one field and should not query the database |

### The three types of CSS

| Type | Where | Why there |
|---|---|---|
| External | `resources/css/style.css` | Shared by every page, cached by the browser |
| Internal | `<style>` block in `lessons.xhtml` | Rules only the scheduling page needs |
| Inline | `style` attributes in `index.xhtml`, `students.xhtml`, `setup.xhtml` | One-off adjustments not worth a reusable class |

---

## 10. Links

- **GitHub repository:** _paste your public repository URL here_
- **Video walkthrough:** _paste your Google Vid link here_

---

## 11. Phase 2 proposals

- Authentication and role-based access for clerks, instructors and managers
- Payment recording and receipt generation using the existing Payment entity
- Theory mock examinations using the existing MockExam entity
- Instructor availability windows, so bookings respect working hours
- SMS reminders to students the day before a lesson
- A calendar view of the schedule rather than a table
- Move database credentials out of `hibernate.cfg.xml` into environment
  variables or a JNDI datasource
