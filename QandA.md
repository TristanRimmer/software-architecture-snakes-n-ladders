# Questions & Answers

This document serves as an evaluation of the Snakes & Ladders product in the form of a Q&A

## Contents

This document will attempt to answer the following key questions:

#### 1. What Design & Architecture Patterns were used?

- Examples of some of the patterns being applied
- Examples of good/bad design and architecture within the code base
- Critical Evaluations on these examples

#### 2. How did parts of the design of the product evolve over time?

- How did the package structure evolve throughout development?
- Were they any major refactors worth mentioning?

#### 3. How well does the code base conform to SOLID Principles?

- Examples of applications of each principle
- Examples of good/bad application of SOLID
- Critical Evaluations on these examples

#### 4. Are there note-worthy insights uncovered in development?

- Examples of code that doesn't necessarily link to architecture or SOLID but is important

Followed by a final evaluative summary of the project

## 1) What Design & Architecture Patterns were used?

Whilst the project makes use of a range of patterns, the development was **strategy pattern driven**

The _names_ of the patterns primarily - and intentionally - used throughout the project were:

- **Strategy Pattern** - This was applied numerous times, almost everywhere.
- **State Pattern** - A finite state machine was created, as required by the assignment brief, to drive the `GameTurn` sequence
- **Adaptor Pattern** - A Port & Adaptors architecture was used - as required for the additional features section in the assignment brief - to drive the "persistant" storage aspect of the product.
- **Observer Pattern** - This was used as an event-tracking system and will be discussed in more detail later
- **Command Pattern** - This was used at the application level to drive the entire flow of the program through `GameAction`s
- **Factory Pattern** - This was **_technically_** used to drive the Hard Coded example. More details later.

### Three Examples

This will discuss 2 good examples of D&A, and one that could be improved upon

#### Observer Pattern - `GameEventPublisher`, `GameEventSubscriber` and `GameEvent`

The pattern works by defining the two interfaces:

```
interface GameEventSubscriber:
  + void notify(GameEvent state)


interface GameEvent:
  ...
```

And the container class

```
class GameEventPublisher:
    - ArrayList<GameEventSubscriber> subscribers
    + GameEventPublisher()
    + void registerNewSubscriber(GameEventSubscriber subscriber)
    + void publish(GameEvent event)
```

Then, in the flow of the game, every time an event occurs, a corresponding packet is sent to the publisher implementation.

This was a good use of design patterns because it allowed the preservation of one-way dependencies and Infrastructure -> Usecase -> Domain architecture, whilst also fulfilling the requriements of statistics tracking and console logging. The design pattern comes with the downside that the various subscribers need to be able to distinguish which event types are being received. This required the use of `instanceof` chains, but that sort of behaviour is baked into the design pattern. By allowing this behaviour, I was able to create application-level implementations which:

- Prints well-formatted text regarding the current game state
- Track the turns of pieces externally. This comes with the additional benefit of fulfilling SRP (Single Responsibility Principle)
- Dice Roll Recording for both debugging use in early development and for file I/O. Once agian, this extracted the tracking necessity away from the DiceRoller classes themselves (SRP)

#### Strategy Pattern - `HitCondition` and `WinCondition`

The strategy pattern can essentially be boiled down to the correct application of interfaces.

`HitCondition` and `WinCondition` were defined:

```
interface WinCondition:
    + WinEvaluationStatus evaluateWinStatus(Piece piece, Board boardProperties)


interface CollisionCondition:
    + CollisionStatus evaluateCollisions(Piece piece, GridPosition oldPos, GridPosition proposedPos,
            List<GridPosition> allCurrentPositions)
```

They correctly updated the state of the pieces depending on their implementation, each returning an enum which were carefully crafted to evaluate the result of the move.

_(For example, regardless of the implementation of the Hit Condition, the result will either mean a `MOVEHAPPENED` or a `MOVEDIDNTHAPPEN`. Similarly, when evaluating whether a piece has won the game, the game is either `WON`, or should `CONTINUE`, or - mainly for the sake of explicitness - had a `CLOSECALL`.)_

This design was simple, handles all game logic correctly, and ensures that the implementation of HitCondition or WinCondition didn't matter to its owner. In my personal opinion, there are quite literally zero down sides to the Strategy Pattern. (You could also argue that a lot of other design patterns all stem from the Strategy Pattern!)

#### `*-Mapper` Objects

This is less to do with a particular application of a design pattern, instead the broader design/architecture of this particular context. I chose to write a custom file system for the File-System implementation of my `GameRepository` interface.
As a result, I needed some methods that could convert the various implementations of the game components to and from strings.
This was fine initially, and broadly speaking they looked like this:

```
class [Game Component]Mapper:
    + static String STRING_IMPL_1;
    + static String STRING_IMPL_2;

    + static WinCondition getImplementationFromString(String string)
    + static String getStringFromImplementation(WinCondition impl)
```

Containing static keys which map to each implementation of the target Game Component, and two methods for serialise/deserialise. The architectural problem came from the fact that the different Game Components have different constrictions,
and this meant that some of the methods would have the potential to throw exceptions, whereas others wouldn't.

For example, the `HitCondition` interfaces can very reasonably be sealed, as there are a finite number of potential HitCondition rules. This means for the `getStringFromImplementation` method that it can confidently cover all bases and thus not need an Exception risk. On the contrary, the `GenerateRandomNumber` interface has many potential implementations and so the interface cannot be sealed. This means the afformentioned method _will_ have an Exception potential.

Therefore, if I wished to put these Mappers into an interface, I would either have to:

- Unseal every sealable interface and add an Exception risk at the interface level
- Seal every interface and restrict the set of implementions on all serialise/deserialise objects
- Add an interface _without_ an exception risk, and on the chance of an unknown implementation, either return a default or throw a RuntimeException

All of these would have felt like a code-smell in their own respect, and as a result, I chose not to define interfaces for these Mappers, and instead they just all look vaguely similar. In retrospect, this doesn't seem **_too_** bad, as something like this _is_ necessary for File I/O, but it feels that perhaps there is a nicer way of doing this which I still don't see.
