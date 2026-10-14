# Feature: cliente-escritorio-swing

## Objective
Desktop Swing client (`04. CLIESC/ESC_CON_UNI_JAVA_SOAP`) for the SOAP unit-conversion server, built with MVC.

## Problem / Why
The client is only a console smoke test over the wsimport stubs. The course needs the three desktop screens (login, convert, catalog) designed in Claude Design.

## Scope
- `modelo/`: `Sesion` (token state), `ConversorServicio` (single wrapper over the wsimport stubs), `ServicioException`, `FormatoResultado` (existing).
- `controlador/`: listens to view events, calls the model, updates the view. Never touches the stubs directly.
- `vista/`: Swing frames only (paint and expose events).
- Generated `ec.edu.monster.ws.*` stubs are never edited by hand.

## Constraints
- Six magnitudes: Area, Longitud, Masa, Temperatura, Tiempo, Volumen.
- Maven via NetBeans: `~/.local/opt/netbeans/java/maven/bin/mvn` (no `mvn` on PATH), Java 25.
- Model must be unit-testable without a running server (stubs are interfaces → fakes).

## Tasks
- [x] T1 Model: `Sesion`, `ServicioException`, `ConversorServicio` + unit tests with fake stubs. Route: inline (3 small files, design already agreed).
- [ ] T2 Smoke test against the running SOAP server (manual).
- [ ] T3 Views: `LoginVista`, `ConversionVista`, `CatalogoVista` following the desktop mockups.
- [ ] T4 Controllers: `LoginControlador`, `ConversionControlador`, `CatalogoControlador` + tests with fake views/service.
- [ ] T5 Entry point: replace console smoke main with the login window.

## Acceptance criteria
- `mvn test` green; controllers and model tested without a server.
- Login error shows the server message; conversion shows `FormatoResultado` text; catalog lists units per magnitude.

## Checks
- `mvn -o test` per task.

## Delivery
Strategy: ask-on-risk. Forecast ~600 authored lines → chain decision asked before crossing ~400.

## Progress
- Baseline: `mvn -o test` green (FormatoResultadoTest, 2 tests) on 2026-10-13.

- T1: RED observed (missing Sesion/ConversorServicio/ServicioException), then GREEN `mvn -o test` 13/13 (ConversorServicioTest 8, SesionTest 3, FormatoResultadoTest 2). Commit `1e2c1b9` on `feat/cliente-escritorio-swing`. Review: medium risk, consent granted, reliability lens approved and acknowledged (lineage review-e8dcd227848df616). Non-blocking follow-ups: login OK with null token should fail; `SOAPFaultException` is a `WebServiceException` and gets mislabeled as "no connection".

## Follow-ups
- [x] F1 Reject login OK without token; map `SOAPFaultException` to its fault message instead of "No se pudo conectar con el servidor". Tests first. RED observed (2 failures), GREEN 15/15. Also pinned `FormatoResultado` symbols to `Locale.ROOT` (review suggestion) and fixed typos in user comments in `Sesion`.

## Design notes (agreed)
- Controllers depend on view interfaces (`LoginVista`, ...), not on `JFrame`, so they are tested with fake views and a `ConversorServicio` built over fake stubs. Controllers come before Swing views.

## Next step
T2 smoke test against the running server, then T3 views.
