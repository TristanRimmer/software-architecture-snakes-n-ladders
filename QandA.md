# Questions & Answers

This document serves as an evaluation of the Snakes & Ladders product in the form of a Q&A

## Contents

This document will attempt to answer the following key questions:

#### 1. What Design & Architecture Patterns were used?

- Examples of some of the patterns being applied
- Examples of good/bad design and architecture within the code base
- Critical Evaluations on these examples

#### 2. How well does the code base conform to SOLID Principles?

- Examples of applications of each principle
- Examples of good/bad application of SOLID
- Critical Evaluations on these examples

#### 3. How did parts of the design of the product evolve over time?

- How did the package structure evolve throughout development?
- Were they any major refactors worth mentioning?

Followed by a final evaluative summary of the project and insights from the module

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
  (Empty)
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

## 2) How well does the code base conform to SOLID Principles?

SOLID details a series of principles which - when adhered to - decreases the technical debt of a code base, improves modularity and extendability, and broadly speaking makes the code base easier to work on.

- **S(ingle responsibility Principle)** - Whilst most classes in the codebase adhere to this rule strictly, a good example in particular is the `PositionTrackingConverter` object. In my opinion, this is the most important principle.
- **O(pen-Closed Principle)** - Game Rules demonstrate this, but the best example of Open-Closed Principle is the Deserialisers
- **L(iskov Substitution Principle)** - With how closely tied this rule is to strategy pattern, this appears almost everywhere in the code base. This is also one of the most important principles.
- **I(nterface Segregation Principle)** - This is demonstrated primarily through the small and simple interfaces from which the game rules are built off. Rather than having larger ones that were broken down, the program was designed with SRP-adhered, small interfaces from day one.
- **D(ependency Inversion Principle)** - The idea that high level concepts should not depend on low level implementations is demonstrated well with the `GameRepository` setup, and partially displayed elsewhere, like the infra-usecase-domain structure and the event system.

### Three Examples

As with design & architecture, I will list and evaluate two good examples of SOLID, and one bad example.

#### Piece & PositionTrackingConverter

Considering the variety of movement patterns possible, I wanted to be very careful to get the Piece objects right.

The naive implementation would have the board own the pieces and handle them as logically, the board owns the pieces. However, this is an unecessary coupling and violates SRP. In my implementation, they are separate objects, and they never _really_ have to interact with each other.

In the first draft, I used the strategy pattern on a `Piece` interface and had a `LowerLeftPiece`, `UpperLeftPiece` etc. These then tracked their position using the 2D grid and handled the serpentine movement. This - however - created a coupling between Piece and the spacial position of it on the board which it ultimately didn't need. Instead, the `Piece` had one implementation called `GamePiece`, which stored everything internally as a displacement - the minimum information the piece needs (Ultimately, it doesn't matter its path, it just needs to know how far it has moved).
It then used a `PositionTrackingConverter` which took on the LowerLeft/UpperRight context to communicate between scalar and vector space.

What this did for SOLID:

- `Single Responsibility` - The Piece no longer needed to directly concern itself with the board's. The Piece also does not track its moves internally, this is offloaded onto a subscriber that listens for game events.
- `Open-Closed`/`Liskov Substitution` - The Piece interface means that the internals can be in whatever format they wish provided it works, and it will still interface with the API. Similarly, If someone wished to make a new `PositionTrackingConverter` that, for example, traversed in column major order, or in a non serpentine movement, they could and it would interfac with the rest of the library.
- `Interface Segregation` - The Piece contains methods for getting its position and setting its position. As it stands right now, though, it also contains a getter for the `PositionTrackingConverter`. This is actually a bit of a code smell, as a Piece Implementation shouldn't necessarily _need_ one. It was added as an after though when implementing file I/O as my Piece implementation stores as `PositionTrackingConverter` compositionally.

#### Dice Rolling & GenerateRandomNumber

Creating the Dice Roller was an interesting problem to tackle structurally, as it was deceptively complicated to get right (For example, initially the dice contained the `java.util.Random` object themselves, which later became wrapped in a `GenerateRandomNumber` interface.)

The structure that I ended up with is a `DiceRolling` interface which was defined:

```
interface DiceRolling:
  + int getNextRoll();
  + int getMaxPossibleRoll();
```

Then, the implementations has the freedom to source their random numbers as they wished. If I instead wished for them to have a `registerRandomNumberSource` for example, this would limit the variety of implementations, violating Interface Segregation Principle. This method instead meant I could create not only the expected `SingleDice` and `TwoDice` implementations, but also for game replaying, `DiceStreamFixed` and `DiceStreamUnbounded` implementations.

What this did for SOLID:

- `Single Responsibility` - the wider architecture with dice rollers did not have to handle their own dice roll tracking, which prevented redundant methods in the DiceStream implementations.
- `Open-Closed`/`Liskov Substitution` - The constraints are so minimal that there are an endless amount of implementations that could be dropped in and immediately work
- `Interface Segregation` - As mentioned above, it gives creative freedom for implementations.
- `Dependency Inversion` - Using a wrapper like `GenerateRandomNumber` separated the low-level `java.util.Random` utility from the domain logic.

#### Board & SpecialLinkedPosition

The best way to describe the life cycle of the Board within the project is feature-creep. Strictly speaking, it fulfils all of its requirements; however, I believe that the `Board` interface is getting quite large

```
interface Board:
    + int getBoardWidth()
    + int getBoardHeight()
    + int getMinimumTravelDistance()
    + boolean pieceHasLandedOnSpecialSpot(GridPosition position)
    + GridPosition getSpecialPositionBehaviour(GridPosition position)
    + boolean registerNewSpecialPosition(SpecialLinkedPositions newPosition)
    + List<SpecialLinkedPositions> getSpecialPositions()
```

From an SRP perspective, there is an argument that this is not violating SRP - board dimensions are obviously board related, the minimum travel distance is closely related to the board dimensions, and the board justifiably owns the special positions. I believe the problem I have is that the number of methods relating to special positions is quite extensive. Perhaps with some careful thought and a refactor, the `pieceHasLandedOnSpecialSpot` and `getSpecialPositionBehaviour` could refactored into one method.
From an ISP perspective, this may still be too big - you could make the argument that the principles should be isolated. Perhaps storing them seperately but using the Board to validate that the special positions on creation would be a nicer architecture from this point of view.

Overall, its probably the weakest interface structure in terms of SOLID Principle fulfilment, but it does still meet some important architectural points:

- `Single Responsibility` - as mentioned above, you can argue it fulfils SRP.
- `Open-Closed`/`Liskov Substitution` - You could create different board implementations and drop them in without a problem, provided it matches the interface
- `Interface Segregation` - the most unfulfilled of the principles. There isn't really any defense in this regard.

## 3) How did parts of the design of the product evolve over time?

The evolution of the products design throughout development can be split into 'Package Structure Changes' and 'Major Logic Refactors'

### Package Structure Changes

The assignment brief specified to follow an 'Infrastructure-Usecase-Domain' package structure. When I began work on this assignment, I didn't _really_ understand it. Part of it comes from my software background, being primarily in lower-level languages (C++/Rust) where this paradigm isn't common practise. As a result, initially, everything was just in
`uk.ac.mmu.game` at a top level. Realistically, this wouldn't have been a problem for _me_, but knowing it would eventually get reorganised, I was lazy with my structure and that slowed down development a lot.

For example, one major change was the refactoring of the afformentioned `java.util.Random` logic out of my domain. Following this, I then struggled with tackling the problem of text output, and it took me a while to figure out the system I eventually used (Subscriber Interface in Domain, Console Logger in Infrastructure)

### Major Logic Refactors

There are a few major refactors that posed significant challenges throughout development. Two major ones were:

#### 1) Piece Vector Spatial Awareness

In the initial version, the Piece class tracked its internal position via a 2-Dimensional Vector. This was a problem for a few reasons:

- Coupled the starting position and the board-position knowledge directly to the piece
- Complicated the movement tracking interally and violated DRY (generally this is not a huge problem, but in this case it was)
- Coupled the game-rule logic tightly with the Piece.

The new version instead had the piece only track its distance from the start as a scalar.
This dramatically simplified movement tracking, and the work for mapping between displacement (scalar) and grid position (vector) was offloaded onto a separate class. This was beneficial from a SOLID persective, and was another implementation of strategy pattern.

#### 2) Game-Rule logic changes

In the initial versions of the game-rules (win condition and hit condition), the interface generally looked like this

```
interface GameRule:
  + bool hasThisGameRuleApplied(positions, etc.., etc..)
```

There were a coupled problems with this:

- It made it very unclear what the boolean return type meant for the implementations. I intended on it meaning that the rule should or shouldn't be applied, but it could easily be interpretted as the rule HAS been or HASN'T been applied.
- Even with the knowledge of how to interpret the boolean, it offloaded the game-state updating into the game-turn logic, essentially meaning the game needed to know the game-rule implementations.

The final versions looked like this:

```
interface GameRule:
  + GameRuleStatus evaluateGameRule(Piece piece, Board board, etc)
```

The `GameRuleStatus` was an enum that made it explicitly clear that the interface/implementation is responsible for handling the pieces. This separated concerns of the game-rule from the game-turn, and instead provided tools purely for making the logic apparent to the user via logging.

## Final Evaluation

### Thoughts

The 'Strategy Pattern' driven development has meant that the game logic is easily extensible at a domain level, allowing for a wide range of extensions to be easily implemented. Abstractions like the `GameTurn`, `GameState` are higher level abstractions which help reduce high-level game logic.

If I were to be critical, the custom Serialise/Deserialise system makes the application, by nature, less pretty. Choosing a custom file type for game saving has required me to make extensive use of `instanceof` which, in any situation at a domain level, would be a serious code smell. As its infrastructure level, its not _as_ serious of a problem, but choosing to use other libraries to handle this for me would have reduced my work load, and the volume of code that had to be written.

Another 'problem' is that it required me to add lazy 'getters' to the game-logic interfaces. In this scenario, it meant that I was updating Domain logic to help infrastructure, which is technically a one-way dependency violation; however, it doesn't directly reference anything outside of domain, from an outsider perspective it isn't, which is why it wasn't refactored.

### Module Takeaways & Summary

The biggest takeaway from the University Module is this package structure. In a higher level language like Java, it is interesting to see a package structure like this and posed a lot of additional concern at development time. This not only directly demonstrated the benefits for having such a dependency separation, but it also revealed gaps in knowledge that a typical Java developer should have.

In summary, I would say that this is a _generally_ very well written code base. It applies SOLID principles to a high standard, and makes good use of design patterns in a way that **ACTUALLY** benefits developers and improves the development cycle, whilst conforming to the 'Infrastructure-Usecase-Domain' package structure. Any violations of SOLID are minor, and would not require major refactors to resolve these issues, and in a project of this size doesn't really pose as technical debt.
