# Demonstrations

This folder contains a list of configs for testing which demonstrate all features as specified in the brief

## Naming Convention

The file called 'basic_game.properties' contains the details of the basic game:

- 2 Player
- No Hit Rule
- Cross the Finish Line to win
- No Teleporters
- 1 Dice

Each other file will, in the name, specify how it differs from the basic game.

For example, "two_dice" will be exactly the same only there are two dice
For example, "mix_teleporters_exact_hit" will be exactly the same but there are a mix of teleport rules, and its exact hit win rule, etc

## Building your own

```sh
# Accepts 'filesystem' or 'memory'

game.repository=filesystem

# Accepts 'ExactHit' or 'CrossTheFinishLine' (same as for filesystem)

game.wincondition=ExactHit

# Accepts 'HitsDoNothing' or 'HitsForfeitTurn' (same as for filesystem)

game.hitcondition=HitsDoNothing

# Accepts 2 or 4

game.numberofpieces=4

# Accepts 1 or 2

game.numberofdice=1

# Accepts 'twowayteleporter' or 'onewayteleporter' or 'mix' or 'noteleporter'

game.teleporterstrategy=mix
```
