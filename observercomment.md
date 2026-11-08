# Observer Pattern Implementations using Payloads

I think there are two issues here, one is an interface segregation issue, and the other is the use of pattern matching to determine the type of the payload.

>  Note, you have called your interface Observer a Subscriber, but the pattern is called Observer and I would suggest however that Pulisher/Subscriber is a term more associated with queues and messaging systems, so I will stick with Observer/Observable for this discussion.


## Interface Segregation

The basic idea of the Observer pattern using payloads is that we create a set of typed immutable data-only payloads (i.e. classes with no behavior) that are passed to the observers. Java Records are a good way of implementing these payloads, as they are inherently immutable.

```Java

public interface Observer {
    void update(ConcretePayloadA payload);
    void update(ConcretePayloadB payload);
    void update(ConcretePayloadC payload);
}

public record ConcretePayloadA(String data) implements Payload {
}

public record ConcretePayloadB(String data) implements Payload {
}

public record ConcretePayloadC(String data) implements Payload {
}
```

Note that payload types become part of the interface specification, and the compiler will enforce that any concrete implementation of the Observer interface  implements update methods for each of the payload types.

```Java
public class ConcreteObserver implements Observer {


    @Override
    public void update(ConcretePayloadA payload) {
        System.out.format("Received ConcretePayloadA with data: %s%n", payload.getDataA());
    }

    @Override
    public void update(ConcretePayloadB payload) {
        System.out.format("Received ConcretePayloadB with data: %s%n", payload.getDataB());
    }

    @Override
    public void update(ConcretePayloadC payload) {
        System.out.format("Received ConcretePayloadC with data: %s%n", payload.getDataC());
    }
}
```

The 'problem' with having a single Observer interface is that it is very easy to add new payload types, but for the payload types become unrelated. For example let us say that one of the payload types is a 'GameStateTransition' and another is a 'DiceRolled' payload. These are unrelated events, and some observers may not want to handle all the events. It would be better to have separate Observer interfaces for each of these event categories, so that I can write different concrete observers for each category that are interested in those events and don't have to implement methods for events that they are not interested in. (This is an example of the interface segregation principle.)

```Java

public interface ObserverOne {
    void update(ConcretePayloadA payload);
    void update(ConcretePayloadB payload);
}

public interface ObserverTwo {
    void update(ConcretePayloadC payload);
}

```
The trick is deciding which payloads belong to which observer interface. One way of deciding in the game example is to check that any class that uses an observer interface is using **all** the methods on the observer interface - if there are some payloads on the interface that are not being used, then it suggests that those payloads belong to a different observer.

Now I can write concrete observers for each category of payload, for example:

```Java
public class ConcreteObserverOne implements ObserverOne {


    @Override
    public void update(ConcretePayloadA payload) {
        System.out.format("Received ConcretePayloadA with data: %s%n", payload.getDataA());
    }

    @Override
    public void update(ConcretePayloadB payload) {
        System.out.format("Received ConcretePayloadB with data: %s%n", payload.getDataB());
    }
}
```
and

```Java
public class ConcreteObserverTwo implements ObserverTwo {

    @Override
    public void update(ConcretePayloadC payload) {
        System.out.format("Received ConcretePayloadC with data: %s%n", payload.getDataC());
    }
}
```

I can of course write a single concrete observer that implements both ObserverOne and ObserverTwo, but the point is that this is an implementation choice that I can change as required (something that is not possible with a single large Observer interface).

```Java
public class CombinedObserver implements ObserverOne, ObserverTwo {

    @Override
    public void update(ConcretePayloadA payload) {
        System.out.format("Received ConcretePayloadA with data: %s%n", payload.getDataA());
    }

    @Override
    public void update(ConcretePayloadB payload) {
        System.out.format("Received ConcretePayloadB with data: %s%n", payload.getDataB());
    }

    @Override
    public void update(ConcretePayloadC payload) {
        System.out.format("Received ConcretePayloadC with data: %s%n", payload.getDataC());
    }
}
```
This is where I think you are going with

```Java
public non-sealed interface GameRelatedEventSubscriber extends GameEventSubscriber {
    void notify(BetterGameStateTransition event);
    void notify(DiceRolled event);
    void notify(TurnChange event);
    void notify(PieceWon event);
}
```

and

```Java
public non-sealed interface PieceRelatedEventSubscriber extends GameEventSubscriber {
    void notify(PieceMove event);
    void notify(PiecesHit event);
    void notify(PieceWon event);
    void notify(SpecialPositionMovedPiece event);
    void notify(TemporaryNewPiecePosition event);
}
```
I would  have these as separate Observer interfaces without the `extends GameEventSubscriber`. GameEventSubscriber has no methods, so it's not really an extends relationship. What you have here is something called a marker interface, which is a way of tagging a class as being of a certain type. In Java, records sometimes do need to be tagged with a marker interface, because Java Records cannot extend another record.

The reason you have done this because you want to use pattern matching in a concrete observer

```Java
    public void publish(GameEvent event) {
        switch (event) {
            case ConfigurableStateRelatedEvent evt -> {
                this.configurableStateNotify(evt);
            }
            case PieceRelatedEvent evt -> {
                this.pieceRelatedNotify(evt);
            }
            case GameRelatedEvent evt -> {
                this.gameRelatedNotify(evt);
            }
        }
    }

   ```

Where as would have been simpler to write

```Java
    public void publish(ConfigurableStateRelatedEvent event) {
        this.configurableStateNotify(event);
    }

    public void publish(PieceRelatedEvent event) {
        this.pieceRelatedNotify(event);
    }

    public void publish(GameRelatedEvent event) {
        this.gameRelatedNotify(event);
    }
   ```

You are also trying to write a single publisher class that will deal with all events. Would it not be simpler to have a separate publisher for each category, matching the segregated observer interfaces discussed above?.

## Pattern Matching

Using a marker interface allows for simple code within the observable to call the observers. Make the interface `sealed` and specify the permitted implementations so that we can  use exhaustive pattern matching to determine the type of the observer and call the correct notify method.

For example this marker interface permits three implementations.

```Java
public sealed interface Payload permits ConcretePayloadA, ConcretePayloadB, ConcretePayloadC {
}
```

The Observer interface can then be defined with a single method that takes the marker interface as a parameter.

```Java
public interface Observer {
    void update(Payload payload);
}
```

The observable class can then dispatch any of the three permitted payloads using the base Payload interface.

```Java
public class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void updateA() {
        // Create a payload and notify observers
        Payload payload = new ConcretePayloadA("Sample data for ConcretePayloadA");
        notifyObservers(payload);
    }

    public void updateB() {
        // Create a payload and notify observers
        Payload payload = new ConcretePayloadB("Sample data for ConcretePayloadB");
        notifyObservers(payload);
    }

    public void updateC() {
        // Create a payload and notify observers
        Payload payload = new ConcretePayloadC("Sample data for ConcretePayloadC");
        notifyObservers(payload);
    }

    public void notifyObservers(Payload payload) {
        for (Observer observer : observers) {
            observer.update(payload);
        }
    }
}
```

We can use pattern matching in the concrete observer to determine the type of the payload and call the correct update method.

```Java

public class ConcreteObserver implements Observer {
    @Override
    public void update(Payload payload) {
        // Implementation for updating the observer
        switch (payload) {
            case ConcretePayloadA a -> handle(a);
            case ConcretePayloadB b -> handle(b);
             default -> handleDefault(payload);
        }
    }


    private static void handle(ConcretePayloadA a) {
        System.out.format("Received ConcretePayloadA with data: %s%n", a.getDataA());
    }

    private static void handle(ConcretePayloadB b) {
        System.out.format("Received ConcretePayloadB with data: %s%n", b.getDataB());
    }

    private static void handleDefault(Payload payload) {
        System.out.format("Unknown Type %s%n", payload.getClass());
    }

}
```

With the pattern matching approach, adding another payload type requires no changes to the Observer interface. However, the concrete observers will still need to be updated to handle the new payload type if we are using a switch statement with exhaustive pattern matching (i.e. no default handler).

Some things to watch for:

- The Payload interface must be declared as sealed, so the compiler can ensure that all permitted implementations are handled in an exhaustive switch statement.
- All permitted implementations of the Payload interface must be marked as final to prevent any further subclassing of the permitted implementations. This is not a problem with Java Records, as they are implicitly final.
- A switch statement must be used. Using if(instanceof) does not allow the compiler to check that all permitted implementations are handled.
- A default case is allowed in the switch statement, which may or may not be a good idea depending on the specific use case.
- It is tempting to put all the handling code in the switch statement, but better to delegate to separate methods for each payload type to keep the handlers separate from the dispatcher.

## Using a full interface with strongly typed methods.

A more traditional approach is to have a full interface with strongly typed methods for each of the payload types. Adding a new payload type will require changes to the Observer interface and all concrete observers. The compiler will enforce that all concrete observers implement all the update methods,

```Java
public interface Observer {
    void update(ConcretePayloadA payload);
    void update(ConcretePayloadB payload);
    void update(ConcretePayloadC payload);
}
```

We can still have a generic dispatcher method in the observable class by using generic lambdas
```Java

public class Observable {
    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void updateA() {
         observers.forEach(observer -> observer.update(new ConcretePayloadA("Sample data for ConcretePayloadA")));
    }

    public void updateB() {
        observers.forEach(observer -> observer.update(new ConcretePayloadB("Sample data for ConcretePayloadB")));
    }

    public void updateC() {
        observers.forEach(observer -> observer.update(new ConcretePayloadC("Sample data for ConcretePayloadC")));
    }


}
```

The lambda expression is equivalent to creating a class that implements Consumer<Observer>. The consumer's accept method calls the update method on each observer with that instance. The compiler creates a different synthetic class for each lambda expression. The following is equivalent code for what the compiler generates for the lambda expression `observer -> observer.update(new ConcretePayloadA("Sample data for ConcretePayloadA"))`

```Java
static class PayloadAConsumer implements Consumer<Observer> {

            private final ConcretePayloadA payload;

            PayloadAConsumer(ConcretePayloadA payload) {
               this.payload = payload;
           }

            @Override
            public void accept(Observer observer) {
                observer.update(payload);
            }
 }
```

## Conclusion

The Interface Segregation principle suggests that we should have separate Observer interfaces for each category of payload.

Using pattern matching allows us to have an Observer interface with a single method, but moves the requirement to handle each payload type into the switch statement. If the switch statement is exhaustive, then you are in effect writing by hand something that the complier will do for you if you have a full interface with strongly typed methods. The switch can have a default case, but then it negates the purpose of having a sealed interface with permitted types, as you can add another permitted type without the compiler telling you to extend the switch. There could good reasons for doing this, but if you have concrete observers that are not interested in all the payload types, then this might suggest that you have your interface segregation wrong.

Using a full interface with strongly typed methods seems the simplest approach and always has the compiler enforcing that all concrete observers implement all the update methods. There are good use cases for pattern matching, but I'm not seeing the benefit of it implementing the Observer pattern, given you can achieve generic dispatching of the payloads to the observers using lambdas.

From a marking perspective, either approach is fine, providing there is a correct implementation of the pattern matching approach.
