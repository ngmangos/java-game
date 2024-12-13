# Assignment II Pair Blog Template

## Task 1) Code Analysis and Refactoring ⛏️

### a) From DRY to Design Patterns 🌵🏜️
[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/5)

> i. Look inside src/main/java/dungeonmania/entities/enemies. Where can you notice an instance of repeated code? Note down the particular offending lines/methods/fields.

- The major instance of repeated code is in the 'move' method of the Mercenary and Zombie.
- Both the zombie and mercenary share identical movement when
    - The player takes invisibility: in this case the mercenary moves randomly, this is indentical to the zombie toast's regular movement
    - The player takes invincibility: in this case both the zombie toast and mercenary move 'away from the player'

> ii. What Design Pattern could be used to improve the quality of the code and avoid repetition? Justify your choice by relating the scenario to the key characteristics of your chosen Design Pattern.

- The best design pattern to reduce repetition in this case would be a strategy pattern. The strategies will each implement the interface 'MovementBehaviour". There will be 4 main strategies.
    - RandomMovement: regular movement of zombieToast, and the by the mercenary when the player is invisible
    - MoveTowardPlayer (Djikstra's):  used by mercenary when not allied with the player and player is not affected by any potions
    - MoveAwayFromPlayer: used by zombieToast and mercenary when player is invincible
    - FollowPlayer: used by the mercenary when allied to the player
- The use of these strategies will be determined by the ZombieToast and Mercenary classes externally

> iii. Using your chosen Design Pattern, refactor the code to remove the repetition.

- The method of each strategy was extracted out of Mercenary's 'move' method
    - The behaviour of RandomMovement was extracted from lines 92 - 101
    - The behaviour of MoveTowardPlayer was extracted from line 132
    - The behaviour of MoveAwayFromPlayer was extracted from lines 102 - 129
    - The behaviour of FollowPlayer was extracted from lines 86 - 87
- It was chosen that the the movementBehaviour did not set the position, but rather just chose the next position of the enemy. This was due to mercenary, where the mercenary sets ifAdjacent based on its next position. This variable of mercenary should not be altered outside the mercenary object.
- In both mercenary and zombieToast, where the method once contained if statements that set the nextPos variable. The if statements now define the movementBehaviour, and the nextPosition is used to set the position.

### b) Observer Pattern 👀

> Identify one place where the Observer Pattern is present in the codebase, and outline how the implementation relates to the key characteristics of the Observer Pattern.

An instance of the observer pattern can be seen between the bomb and switch entities. In this instance the bombs act as the observers and the switch the subject, where the switch notifies the bomb of its state change and it is updated accordingly. The many-to-one relationship is held as multiple bombs can be subscribed to one or more switches (held in isolation each switch can have multiple subscribers, even if subscribers overlap between switches).

- In this instance, the switch is the subject and it notifies the bombs when its state changes (if it is activated) within the subscribe(Bomb bomb, GameMap map) and the onOverlap(GameMap map, Entity entity) method. It notifies the bombs via the notify method in Bomb.

- A bomb can be subscribed to multiple switches as represented by the subs field (a list of switches), when a switch is activated it "observes" the state change and is updated accordingly (explodes and updates the game map's state accordingly).

### c) Inheritance Design 👨‍👩‍👦‍👦

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/2)

> i. Name the code smell present in the above code. Identify all subclasses of Entity which have similar code smells that point towards the same root cause.

The code smell present in this code is refused request, where a sub-class inherits methods from its parent that it won't use because it doesn't need them. In this case, the Entity class creates onOverlap, onMovedAway and onDestroy as abstract methods, meaning its sub-classes must implement the method to fit their own needs, but most of the time it just means an empty method is created. This results in these methods being needlessly repeated in every entity subclass,

All subclasses of Entity only require some or none of these methods.
1. Entities that implement none: all Buildables, Exit and Wall.
2. Entities that implement only onOverlap: Collectables, Boulder, Door, Player, Portal
3. Entities that implement onOverlap and onDestroy: Enemies
4. Entities that implement onOverlap and onMovedAway: Switch
5. Entities that implement only onDestroy: ZombieToastSpawner

> ii. Redesign the inheritance structure to solve the problem, in doing so remove the smells.

To fix this code smell I plan to change the Enitity class to define the default methods for these actions as nothing happening (meaning they return void):
1. Change onOverlap, onMovedAway and onDestroy to be defined methods that return void in Entity.
2. Remove all instances of these methods being overidden in Entity's subclasses by the unimplemented methods. Any methods that have specific implementation take advantage of polymorphism and override the defaults.

As a result, if there is an entity that has no behaviour when it is destroyed (for example), it can simply inherit the empty method to fit this scope. If this same entity needs specific logic when there is an overlap it can Override the superclass' method and define its own logic as needed. Essentially, entities only need to implement these methods when it has specific logic, not for every instance.
 
 
### d) More Code Smells 💨👃
[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/7)

*Collectable entities are a big problem. We tried to change the way picking up items is handled, to be done at the player level instead of within the entity itself but found that we had to start making changes in heaps of different places for it to work, so we abandoned it.*
*Collectable entities are a big problem. We tried to change the way picking up items is handled, to be done at the player level instead of within the entity itself but found that we had to start making changes in heaps of different places for it to work, so we abandoned it.*

> i. What design smell is present in the above description?

- The code smell in the above statement is shotgun surgery. Meaning that to implement one small change, many other changes must also be made.
- The code smell in the above statement is shotgun surgery. Meaning that to implement one small change, many other changes must also be made.

> ii. Refactor the code to resolve the smell and underlying problem causing it.

- The underlying problem in the code seems repetition of identical methods. For example, almost all collectible entities (including buildable items) share the same methods of canMoveOnto, onMovedAway, onDestroy and onOverlap. For the above example, the method onOverlap places the heaviest burden, as each collectable entity overrides the Entity class' onOverlap method to each provide an independent but identical implementation of the player overlapping with and picking up items. As the implementation of this function is identical, if this method could be shared by all of the classes through inheritance, it would make future modific ations much more simple.
- To implement this change:
    - Make a abstract class Collectable.java that is the super class of all collectable entities. This abstract class implements the shared methods of
    the collecable entities. Most importantly, onOverlap and canMoveOnto.
- The underlying problem in the code seems repetition of identical methods. For example, almost all collectible entities (including buildable items) share the same methods of canMoveOnto, onMovedAway, onDestroy and onOverlap. For the above example, the method onOverlap places the heaviest burden, as each collectable entity overrides the Entity class' onOverlap method to each provide an independent but identical implementation of the player overlapping with and picking up items. As the implementation of this function is identical, if this method could be shared by all of the classes through inheritance, it would make future modific ations much more simple.
- To implement this change:
    - Make a abstract class Collectable.java that is the super class of all collectable entities. This abstract class implements the shared methods of
    the collecable entities. Most importantly, onOverlap and canMoveOnto.

### e) Open-Closed Goals 🚪

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/3)

> i. Do you think the design is of good quality here? Do you think it complies with the open-closed principle? Do you think the design should be changed?

I do not think the design is of good quality due to the use of switch statements in the methods achieved(Game game) and toString(Game game). Switch statements are one of biggest signs of violating the open-closed principle as it means that the original code is open for extension but also open for modification (as to extend the code's functionality, modifications must be made to the original file in the form of extra switch statements).

> ii. If you think the design is sufficient as it is, justify your decision. If you think the answer is no, pick a suitable Design Pattern that would improve the quality of the code and refactor the code accordingly.

A suitable design pattern to fix this flaw would be the composite pattern. As goals can be composed using boolean logic to form compound goals such as AND and OR they would form the composite nodes and the goals themselves would be the leaf nodes, each different goal being a different leaf node.

I converted the goal class into an interface where the leaf and composite nodes would implement the methods achieved and toString, which was simply done by pushing their respetive methods from the witch statements originally in Goal to their respective classes. I also pushed the constructors for creating distinct goal types into their class (only treasure and and or require specific constructors the other classes utilise the default constructor).

### f) Open Refactoring 🛠️

[Merge Request 1](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/8)
- As the entities Bow and Shield cannot be placed on the map, they don't use many of the methods within the Entity class. It is invalid that they have these unusable methods. For this reason, all methods within the Entity class related to the map or position were transferred to a new class called PlaceableEntity. The only method left in Entity was the function getId. All entities that can be placed on the map now inherit the PlaceableEntity class, and all methods in the system that use the Entity Class while also using the map now call the Placeable Entity class.
- The buildables class was deleted as it had no methods within it, and was no used anywhere within the code

[Merge Request 2](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/9)
- Removing the deprecated function PlaceableEntity.translate(Position offset) as it was marked for removal and made obsolete. The function PlaceableEntity.translate(Direction direction) was simplified and placed into a new function called PlaceableEntity.movePosition.
- The latter function was only used in the GameMap method moveTo.  Although the comment above the function stated that it could be replaced by setPosition. If setPosition was used in the GameMap.moveTo method, the position of the entity would have to be called, the new position would have to be calculated with the direction and then setPosition would be called to enter it back into the entity.
    - This is high coupling. As the position is a property of the entity, it should only be modified by the entity.

[Merge Request 3](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/10)
- One of the issues i found with the initial system was in the buildable items. Determining if the ingredients to construct an item was present and removing the ingredients from inventory when an item was made were performed entirely by the inventory. On top of this, determining these were also hardcoded
    - Look at Inventory.java lines 38-42 and 51-72 of the original repository
- It should be the responsibility of the bow or shield class to provide their ingredients. Therefore these the Bow and Shield class now contain static methods to determine if the inventory contains the sufficient ingredients and to provide the inventory with the ingredients needed to be built.
- Also, the required ingredients should be located in a single, easily modifiable place. By now have list constants of the required
ingredients, this issue is mended.

[Merge Request 4](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/15)
- The initial state pattern of the player to determine the potions in effect was not correctly implemented and contained
code smells
- Firstly, the states contained unecessary functions and data
    - The states contained an address to the player, but it was not used
    - Both the player and the states contained independent functions to transition to other states
- Secondly, the player had a state pattern to determine there current potion state, but they also held a variable of the address of the current potion in use. This introduced two code smells.
    - It was an example of code repetition. As both determine the current condition of the player, only one is needed
    - The potion variable was extracted and used by other classes to determine the state of the player. This is an example of high coupling, as the functions rely on internal information about eachother.

Modifications
1. After research (refactoring guru), it was determined that state transitions should be implemented by the player and not by 
        the states themselves. This change was implemented first.
2. Each of the states was gutted completely. The only function of the states was in their superclass to determine the buffs of the player, allowing the state pattern to conform to the open-closed principle as new classes could be introduced without modification.
3. The current potion (inEffective) variable was removed entirely, and all requests to determine the buffs of the player were mediated by the state pattern

[Merge Request 5](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/17)
- The implementation of spider to determine whether the spider can move in its current trajectory is cumbersome and is a form of repetition
    - This is clear in lines 58-62, where the spider requests all entites on the current square, determines if the return is null,
    calculates the size of the entities list ...
- This code is unecessary as there exists a function is GameMap called canMoveTo that already determines whether an entity can move onto
a position on the map
- Similarly in lines 172 and 226 of GameMap.java, the map requests for the node, then uses the nodes method of canMoveTo. As the helper function
already exists, these should just use the helper function.

[Merge Request 6](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/18)
- The current implementation of hasKey in door.java exhibits the code smell of innapropriate intimacy as the door needs to know the details of the players inventory to run this method.
- To fix it we simply move the hasKey method into the player function (as it is the player's repsonsibility to know if it has a key and if it is the correct one), where it simply takes in the door instead of the player (so the player knows if the key fits!).

[Merge Request 7](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/20)
- The current implementation of interact in zombieToastSpawner has a long string of method chaining represening feature envy from the zombieToastSpawner class leading to high coupling between this class and the player class (relying heavily on the methods from the player class).
- To fix this code smell we can simply move this method into the player class (I chose to rename it to zombieToastSpawnerInteract for increased clarity of what the player is interacting with). Now the interact method in the zombie toast spawner class will call the new one in the player class, minimalising coupling.

## Task 2) Evolution of Requirements 👽

### a) Microevolution - Enemy Goal 🧟

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/14)

**Assumptions**

- There were no notable assumptions made for this task

**Design**

The design of enemy goal is quite simple, building on the solid foundations of the goals package and composite pattern
- Enemy goals are set at the start if the game with a target determining the amount of enemies that need to be defeated
- The player stores the amount of enemies defeated
- In Game.battle, if the enemy dies, the enemies defeated counter stored in the player is incremented
- The response builder loops through the goals and when determining if enemy goal is achieved, the enemy goal determines 
if the enemy defeated count stored by the player is greater than or equal to the target of the enemy goal.

**Changes after review**

- No major changes were made to the design after consulting with Keanna on wednesday

**Test list**

- Does the enemy goal when writing more complex goal equation
- Can the enemy goal be modified

### e) Snakes 🐍

[Links to your merge requests](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/12)

**Assumptions**

- The player can only attack one body part or head at a time
- The health of each body part / head is independent and equal to initial JSON health + any bonuses
- A snake body part cannot be created with a current next (must be null), snake body must always be created at end of snake
- A snake head part cannot be created with a current previous (must be null), snake head must always be created at beginning of snake
- Snakes can only spawn at the beginning of the game from JSON file similar to the implementation of mercenary

**Design**

- Snake head and snake body are created as subclasses of Enemy.
    - Snake body is an observer of Snake head subject
    - Snake body are subjects to other snake body behind it
        - This is for in the case the snake body dies
- Snake head movement is determined by:
    - determine all reachable food items
    - BFS to find closest food item and determine path
- For snake body movement
    - snake body is assigned another body part/head when it is created
    - snake body will always move onto lastDistinctPosition of its assigned body part / head
- Each time the snake moves it determines whether it has overlapped with a food item
- If snake overlaps with food item: 
    - food item removed off map
    - snake body created
        - recursive function determines position of snake body, and body part before it
    - food bonus added to snake
        - observers are updated
        - all snake body parts have local health and attack that is updated
            - as all snake body parts can be individually attacked and die, they should each have individual health
- If snake body dies while snake is invincible
    - body part is removed
    - adjacent body part is transformed into head
        - observers of new head are the same as it was when it was a body part
        - observers are removed from the original head

**Review by partner**

- Keanna was concerned about use of null in the structure of the snake (eg ln 74-79 of SnakeBody.java)
    - We weighed the idea of using tail subclass of snakeBody, but this strategy seemed to be ineffective due to the name converter
    - For the 'follow' function in particular, we also weighed the idea to utilise the observer pattern. But due to the nature of 'follow'
    the parts of the snake must move in exact order
        - as the function sets the position to previousDistinctPosition of its upstream (closer to head), if the snake parts moved out of turn, a downstream snake part would not move as its previous

**Test list**

- Test the movement of the snake between two food items, the snake move toward the closest
- If an invincible snake body part is killed, the adjacent snake body after it should transform into a snake head. 
    - If the closest food item is in the direct path of the player, the new snake should move onto the player's square during the same tick and die.
    - If there is a food item that is closer to the head of the new snake than the original snakes current destination, and its path is not in the direct path of the player. The new snake should instantly change directions in the same tick as its creation to reach the closer item.
- Determine whether a snakes fighting odds change after eating a key, treasure or arrow
- Test if an invisible snake will move towards a closer food item that is surrounded by a wall rather than a food item in its direct path.
- Test if a non-invisible snake will not move if its path is blocked by another snake
- Test if an invisible snake will move even if its path is blocked by another snake

### f) Logic Switches 💡
[Links to your merge requests: part_2f](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/13/)

**Assumptions**
- Logic bombs are not conductors, i.e. they don’t transmit current through themselves.
- Spiders can move through switch doors as they can with regular doors.
- Following design by contract, we assume that invalid inputs from the user won’t be entered.

**Design**

(Updated method signatures with newer versions).

[Links to your merge requests: part_2f_prototypes](https://nw-syd-gitlab.cseunsw.tech/COMP2511/23T3/teams/W12A_CATERPIE/assignment-ii/-/merge_requests/13/)

###### Two new packages:

1.package dungeonmania.entities.logicals;

2.package dungeonmania.entities.logicals.strategies;

Package 1 stores the new objects for this task (LogicBomb, SwitchDoor, LightBulb, Wire), it also stores the second package under folder strategies containing the different logic rules for the task (And, Or, Xor, CoAnd).

*Strategy Pattern* - To implement the different logic rules, I will implement a strategy pattern consisting of:

- Interface LogicalRules with field booleanlogicStrategy(List<conductors> conductors);
- The different strategies implmenting this method (and, or, zor and co_and).
- Each logical entitiy (LogicBomb, LightBulb, SwicthDoor) has a field for their strategy called logic.

*Observer Pattern* - Following from the already existing observer pattern between bomb and switch I will implement an observer pattern between switch (the subject) and logical entities (the observers). This will consist of:
- An interface called LogicalObserver that has a notify method that is called when a switch is activated to update the game state.
- A method called addAdjacentConductor which will update what Conductors (switches and wires) its connected to.
- LightBulb, SwitchDoor and LogicBomb will implement this interface as they are directly affected by the switches states.

*Conductor Interface* - a common interface between wires and switches for ease of access between them.
- Contains method isActivated to see if the switch or wire is currently conducting electricy/is activated.

*LogicalObservser* - Interface shared between the LightBulb, SwitchDoor and LogicBomb classes.
- These classes inherit attributes from PlaceableEntity (LogicBomb is specifically a subclass of bomb).
- They contain the fields: logic (to store its logic strategy), isActivated (to store its state) and adjacentConductors (to store the conductors surrounding it that will directly affect if it will be activated or not.
- They override methods from their superclass where needed (behaviour identitified in specificication).

I chose not to implement another state pattern for these entities being activated or not as it is much simpler to store two states as a boolean rather than create entire classes (overcomplicting the system) for states with no specific functionality.

In GameMap:

- Add a method called bfsLogicFind(Switch s) that finds logic entities connected to a switch (when it is activated) directly or through wires and adds them to the switch's list of connected logicals, also adds any adjacent Conductors to a logical entity's list of conductors.
- May need helper functions to make function less long or combine repeated code.

**Changes after review**

- Nick viewed my deisgn and pointed out that with the current design, implementing the co_and logic would probably not be possible or easy. He came up with adding a tickActivated field in Switch and Wire that tracks what tick these entities are activated in, allowing for co_and to be implemented very easily.
- Conductors will be activated and their tickActivated field will be set when they get referenced in the bfs function (as it is only called when a switch gets activated).

**Test list**

To test logic strategies we create:
- a map with only lightbulbs
- a map with only switchdoors (test if player can walk through an activated door and not through a closed one)
- a map with only bombs (test specific cases when bomb gets picked up and put down, when bomb destroys circuit other entities should be updated accordingly).
with each kind of logic field attatched. These maps will have wires linking to the LogicalObservers (wires are linked in such a way that it allows for multiple cases of each startegy to be tested (e.g. 2 and lightbulbs, one has one wire connected, one has two, the second one should light up when activated)). Test implementation will involve swicthing on and off linked switches.
- A test that can be done in any test case is checking the player's position to ensure they can walk on wires.


## Task 3) Investigation Task ⁉️

**Enemies Movement Restrictions**
[Merge Request 1](/put/links/here)

MVP spec requirements
- Zombies: 
>Zombies are limited by the same movement constraints as the Player, except portals have no effect on them.
- Mercenaries: 
>Mercenaries are limited by the same movement constraints as the Player.
- Spiders: the spec is unclear whether they can move onto a space that holds a player. But in the original implementation it inherits the canMoveOnto function from the Enemy class,  so the assumption can be made that it hold similar movement constraints as the other enemies.

Original implementation
- All enemies share the same canMoveOnto function, only allowing players to move onto them.
- This implementation doesn't follow the spec. In the spec the player can move onto squares containing enemies. So if mercenaries and zombies are *limited by the same movement constraints as the player*, they should be able to move onto squares with other enemies.

Updating implementation
- The canMoveOnto function in the enemy abstract class allows enemies to move onto eachother
    - For this we assume that the spider move similarly to other enemies 
    - *note that the spawn restraints of the spider is different to its move restraints, as the spider cannot spawn onto the player, but can move onto the player*
- Snake still implements independent methods of canMoveOnto (as it cannot move onto other snakes without being invisible), but enemies will now be able to move onto the snake

