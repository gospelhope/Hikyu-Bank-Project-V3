# Short-path verification

Verified on 2026-10-02 with Java 17.

- Clean Maven package: 12 existing backend integration tests passed.
- Frontend HTTP client tests: 3 passed.
- Packaged JAR: health, frontend asset serving, cookie-based email OTP/PIN login,
  3 seed accounts and 12 transactions verified.
- Production classes generated under target/classes/hikyubank/.
- Every original Java source is identical after reversing only the package-name
  replacement hikyubank -> com.hikyu.bank.
- Frontend source, main resources, launchers and demo data are unchanged.

The source paths and package/import names changed; business logic did not.
Native macOS/Windows launchers and browser visual appearance were not rechecked.
