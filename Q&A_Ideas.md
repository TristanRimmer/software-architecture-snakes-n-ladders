# Q & A

This should not be representitive of the final product, right now its just a list of thoughts

## Which design and architecture patterns did you use and why?

### (you should include examples from your software product to illustrate your use).

- Strategy Pattern (literally everywhere)
- Observer Pattern (GameEventPublisher and subscribers)
- Command Pattern (GameAction)
- State pattern (GameState)
- Adaptor Pattern (GameRepository is a port, and its impls is an adaptor implemnenting the port)
- Factory Pattern - technically implemented, not really making much usage of it (HardCoded(\*)Factory objects)

## Which element(s) for your design and architecture are you most pleased with and why?

Piece & PositionTrackingConverter separation (especially compared to the old version) is nicely done

GameEventPublisher is a perfect way preserve the Infra-UseCase-Domain architecture whilst fulfilling all requirements like console output

## Which element(s) for your design and architecture are you least pleased with and why?

Board interface is a little large.

There are arguments for and against it violating SRP

The Mapper objects are a little strange - they dont really suit an interface but they sort of do?

Generally speaking, the custom serialisation means a lot of code has been added that CANNOT conform nicely to principles (eg mapping an impl to a string)
An external library will also have to do this, they just hide the code for it

### What could you do to improve those elements if you had more time?

Possible decoupling

## Describe how your design evolved over time.

- Piece & PositionTrackingConvert decoupling
- GameTurn implementation
- Board Refactor
- Win and Hit Condition refactor to update the piece themselves

## How do you think your code follows the principles we have discussed in the module?

S(ingle Responsibility Principle) - Piece & PTC de-coupling
O(pen-Closed Principle) - GameState options is a good example
L(iskov Substitution Principle) - Strategy Pattern
I(nterface Segregation Principle) - Interfaces are all small and neat
D(ependency Inversion Principle) - Game Repositories -> the high-level policies dont depent on low-level implementations and instead depend on this abstraction

### (usexamples from your software product to illustrate where you think principles have or have not been followed well).

## Can you identify insights you have personally gained about software design and architecture from this module?

### How did you arrive at your insights (you might need to go back through your Git history, lab exercises or notes looking for ‘lightbulb moments’).

- Piece & PositionTrackingConvert decoupling
