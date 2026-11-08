# Snakes and ladders

## _Software Design & Architecture_

My implementation of Snakes & Ladders, following the assignment briefs instructions regarding design choices and package structure

## Usage

The program comes with to versions available:

- Hard Coded Java version
- Spring-Boot Dependency Injection version

To run:

```sh
mvn exec:java
```

Or

```sh
mvn spring-boot:run
```

### Note

The Dependency Injection will only affect how any new games are created. If you load a game from memory/file, it will correctly load that games settings.


### File System

If you choose to use the File System Game Repository, `demonstrations/sandl_files/` contains a series of files to drop into the game's storage path.

The storage path will either be at `$HOME/MMU_24854995_SandL/` or `$PWD/MMU_24854995_SandL`
