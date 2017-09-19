# AuctionHouse
AuctionHouse is a generic implementation of an auctioning system, where players can offer their items for sale. Players can choose which type of auction they want, either BUYING or SELLING, as well as setting the price for the transaction, and the amount of items they are trading.

## Compile Requirements
The following projects are required to compile this project:
- [Gradle-Super](https://github.com/IslesSoftworks/Gradle-Super)
- [Bukkit-Gradle](https://github.com/IslesSoftworks/Bukkit-Gradle)

Note: This requires [Gradle](https://gradle.org) as your build automation system.

Clone this project to your local directory, and run fatJar to obtain a copy of the jar.

## Installation
Once you've compiled the fatJar, upload the jar to your server's plugin folder. Restart your server to generate the config.yml.

Open the config.yml and enter the MySQL database connection details.

Copy/paste the [database schema](https://github.com/Unknowncmbk/auction-house/blob/master/setup/database-schema.txt) tables into your MySQL database.

Once this is done, restart your server again.

## Results
Players can use the following commands:
- `/bal`: Checks the player's balance.
- `/auctionhouse`: Opens the auction house.

If the player opens the Auction House menu, this is an example GUI that they will see:
- ![alt text](https://github.com/Unknowncmbk/auction-house/blob/master/img/auction_menu.png "Auction Main Menu")

If the player wishes to add a new auction to the Auction House, they can use the builder:
- ![alt text](https://github.com/Unknowncmbk/auction-house/blob/master/img/create_offer.png "Auction Builder")

If the player wishes to change their offer type, this is an example of the BUY offer:
- ![alt text](https://github.com/Unknowncmbk/auction-house/blob/master/img/auction_type.png "Buy Offer")

