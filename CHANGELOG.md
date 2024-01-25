# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.5.0 - 2024-01-24
### Changed
- Update to latest Apache SSH library to fix CVE-2023-48795 (Terrapin)

## 2.4.0 - 2023-11-17
### Added
- Option to specify whether SSH is the preferred checkout variant

## 2.3.4 - 2024-01-25
### Changed
- Update to latest Apache SSH library to fix CVE-2023-48795 (Terrapin) (Backport from 2.5.0)

## 2.3.3 - 2022-08-05
### Fixed
- Prevent host key algorithm from automatic changes ([#46](https://github.com/scm-manager/scm-ssh-plugin/pull/46))

## 2.3.2 - 2022-08-02
### Fixed
- Authorized key authentication with Ubuntu 22.04 (upgrade of apache sshd library, [#45](https://github.com/scm-manager/scm-ssh-plugin/pull/45))

## 2.3.1 - 2022-05-13
### Fixed
- Replace custom styling with link styled button ([#38](https://github.com/scm-manager/scm-ssh-plugin/pull/38))

## 2.3.0 - 2021-01-08
### Added
- Unify and add description to key view across user settings ([#13](https://github.com/scm-manager/scm-ssh-plugin/pull/13))
- Support ed25519 keys ([#16](https://github.com/scm-manager/scm-ssh-plugin/pull/16))

## 2.2.1 - 2020-11-06
### Fixed
- Disable cloning repositories via ssh for anonymous users ([#12](https://github.com/scm-manager/scm-ssh-plugin/pull/12))

## 2.2.0 - 2020-09-25
### Added
- Add access token command ([#11](https://github.com/scm-manager/scm-ssh-plugin/pull/11))

## 2.1.0 - 2020-08-20
### Added
- Documentation in English and German ([#6](https://github.com/scm-manager/scm-ssh-plugin/pull/6))

### Fixed
- Anonymous users should not be able to access or modify authorized keys ([#10](https://github.com/scm-manager/scm-ssh-plugin/pull/10))

## 2.0.0 - 2020-06-04
### Changed
- Changeover to MIT license ([#5](https://github.com/scm-manager/scm-ssh-plugin/pull/5))
- Rebuild for api changes from core

## 2.0.0-rc3 - 2020-03-13
### Added
- Add swagger rest annotations to generate openAPI specs for the scm-openapi-plugin. ([#4](https://github.com/scm-manager/scm-ssh-plugin/pull/4))

## 2.0.0-rc2 - 2020-01-29
### Added
- Configuration flag to disable password authentication

## 2.0.0-rc1 - 2019-12-02
