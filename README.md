An ant algorithm simulation with JavaFX.

Download link to the jar file is below (includes a graph file and translation for UI)
(Note: "graph" file is drawn in 1920x1080 resolution and requires a screen with minimum 1280x720 resolution to display)

https://www.dropbox.com/s/yi8cdh7737ijixb/Ants.zip?dl=0

Summary of the implementation and variables:

Q is the amount of pheromone that an ant releases behind.

Kn means the length of the entire route that an ant traveled between the cave node and the food node. Remember, this route is not supposed to be direct!

Delta F means the amount of pheromone to be released according to the route length. So if the route is too long, this value is gonna be a small number.

F(t) means the amount of the pheromone on the route. F(t+1) means the new amount of the pheromone on the route to be assigned.

b is the evaporation coeffient. Small number means most of the pheromone is puff... gone.
Basically new pheromone amount is old amount multiplied with coefficient, plus released amount / entire route length.

Fij means pheromone between i and j. In my coding, i is always equals food and j equals cave. The animal leaves cave, doesn't drop pheromone until it finds food, animal finds food, goes back to cave, drops pheromone onto entire route that it traveled back, tries to find food again without dropping pheromone, and cycle goes on...

Pij means the probability of an edge (a line that connects one node to another) to be selected as next direction.
So you know Fij, the pheromone amount on that link and Nij is the length of that link.

Alpha and Beta is control parameters. İf alpha is high, the ant becomes so inclined to track the pheromone. Maybe because it smells good :)
İf beta is high, the ant becomes so inclined to select the shortest link (edge). You should experiment these. This is the purpose of this program :)

Finally the number you find by multiplying pheromone and link length should be divided to the total.
Which means, after the division, one link is gonna have more weight (probability) compared to others.
Basically quality of link is divided with the total quality of all links.
İf you add up al the possibilities, it should be equal to 1.
