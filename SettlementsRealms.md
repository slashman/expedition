# Introduction #

This file will be used to fill the expedition world map with hundreds of cities, based on the history period being played!

# How to help #
  * Take a look at the map and pick a city
  * Lookup wikipedia for that city, note its location
  * Build a history record for the city, each record has a date and may have the following:
    * A population record
    * A realm change
    * A name change.
    * A "join" event with another settlement
    * A "abandoned" event
  * Event records must have a "src" attribute, citing based on why it was defined, and if they use approximate facts, they should use the "approximate" attribute
  * Realms are similar, but their history record are just for joining another realms

The specification is WIP, please comment for anything you feel should be improved.

# Definitions #
Settlement: A settlement [...] is a permanent or temporary community in which people live [...]. The term may include hamlets, villages, towns and cities.

State: In the social sciences, a state is the compulsory political institution of a centralized government that maintains a monopoly of the legitimate use of force within a certain territory

Confederation: A confederation is an association of sovereign member states that, by treaty, have delegated certain of their competences (or powers) to common institutions, in order to coordinate their policies in a number of areas, without constituting a new state on top of the member states


# Useful links #
  * [Europe\_about\_1560](http://upload.wikimedia.org/wikipedia/commons/2/2c/Europe_about_1560.jpg)
  * http://en.wikipedia.org/wiki/Historical_cities

# Example 1: Palos de la Frontera, Spain #

```

<geopolitical>
  <settlements>
    <settlement id = "PALOS" lat="37°14'N" long = "6°54'W" patron = "java.koder@gmail.com" lastUpdate = "23/02/2011">
      <population>
        <sample date = "1120" pop = "10" approximate/>
        <sample date = "1322" pop = "100" approximate/>
        <sample date = "1475" pop = "2500" src = "Fishing and seafaring expeditions to Guinea" approximate/>
        <sample date = "1550" pop = "700" src = "Establishment of the Casa de Contratación at Seville in 1503" approximate/>
        <sample date = "1700" pop = "125" src = "By the 18th century, the town had only about 125 inhabitants"/>
        <sample date = "1750" pop = "2500" src = "Catalan investors established a viticultural industry centered at Palos" approximate/>
        <sample date = "2006" pop = "8415" src = "According to the 2006 census"/>
      </population>

      <history>
        <event date = "1120" state = "ALMOHAD" name = "Palos"/>
        <event date = "1248" state = "CASTILE"/>
      </history>
    </settlement>
  </settlements>
  <states>
    <state id = "ALMOHAD"/>
    <state id = "CASTILE">
      <history>
        <event date = "1035" name = "Castile"/>
        <event date = "1230" name = "Castile and Leon"/>
        <event date = "1516" join = "SPAIN"/>
      </history>
    </state>
    <state id = "LEON">
      <history>
        <event date = "910" name = "Leon"/>
        <event date = "1230" join = "CASTILE"/>
      </history>
    </state
    <state id = "SPAIN">
      <history>
        <event date = "1516" name = "Spain"/>
      </history>
    </realm>
  </realms>
</geopolitical>

```

# Example 2: Medellín, Colombia #

```

<geopolitical>
  <settlements>
    <settlement id = "MEDELLIN" lat="6°14'9.33N" long = "75°34'30.49W" patron = "java.koder@gmail.com" lastUpdate = "22/02/2011">
      <history>
        <event date = "1616" pop = "100" name = "Poblado de San Lorenzo" src = "In 1616 the colonial visitor Francisco de Herrera y Campuzano founded a settlement with 80 Amerindians" approximate/>
        <event date = "1574" pop = "1000" src = "After 1574, with Gaspar de Rodas settled in the valley, population started to grow." approximate/>
        <event date = "1675" pop = "3000" name = "Villa de Nuestra Señora de la Candelaria" src = "In 1675 the first census during colonial times was taken: there were 3,000 people and 280 families"/>
        <event date = "1787" pop = "14507" src = "Antonio Mon y Velarde ordered one between 1786 and 1787: there were then 14,507 people and 241 families"/>
        <event date = "1808" pop = "15347" src = "In 1808, two years before Colombia won independence, the city had 15,347 people and 360 families"/>
        <event date = "1819" realm = "COLOMBIA"/>
        <event date = "1905" pop = "59815" src = "In the first half of the twentieth century, the population of Medellín increased sixfold, from 59,815 inhabitants in 1905 to 358,189 in 1951."/>
        <event date = "1951" pop = "358189" src = "In the first half of the twentieth century, the population of Medellín increased sixfold, from 59,815 inhabitants in 1905 to 358,189 in 1951."/>
        <event date = "2005" pop = "2636101" src = "Census"/>
      </history>
    </settlement>
  </settlements>
  <realms>
    <realm id = "SPAIN"/>
    <realm id = "COLOMBIA"/>
  </realms>
</geopolitical>

```

## Example 3: Milwaukee, USA ##

```
<geopolitical>
 <settlements>
  <settlement id = "JUNEAUTOWN" lat="43°03'N" long="87°57'W" patron = "gamer2k4@gmail.com" lastUpdate = "22/02/2011">
   <history>
    <event date = "1825" pop = "100" realm = "WISCONSIN TERRITORY" name = "Juneautown" src = "Founded by Solomon Juneau east of the Milwaukee River; population is approximate" approximate />
	<event date = "1830" pop = "400" approximat e/>
	<event date = "1835" pop = "800" approximate />
	<event date = "1840" pop = "1600" approximate />
	<event date = "1845" pop = "3200" approximate />
	<event date = "1846" join = "MILWAUKEE" src = "Combinined with Kilbourntown and Walker's Point to make Milwaukee"/>
   </history>
  </settlement>
  <settlement id = "KILBOURNTOWN" lat="43°03'N" long="87°58'W">
   <history>
    <event date = "1826" pop = "100" realm = "WISCONSIN TERRITORY" name = "Kilbourntown" src = "Founded by Byron Kilbourn west of the Milwaukee River; population is approximate" approximate/>
	<event date = "1830" pop = "300" approximate/>
	<event date = "1835" pop = "600" approximate/>
	<event date = "1840" pop = "1300" approximate/>
	<event date = "1845" pop = "2800" approximate/>
	<event date = "1846" join = "MILWAUKEE" src = "Combinined with Juneautown and Walker's Point to make Milwaukee"/>
   </history>
  </settlement>
  <settlement id = "WALKER'S POINT" lat="43°02'N" long="87°57'W">
   <history>
    <event date = "1834" pop = "50" realm = "WISCONSIN TERRITORY" name = "Walker's Point" src = "Founded by George H. Walker when he built a cabin south of the Milwaukee River; population is approximate" approximate/>
	<event date = "1838" pop = "200" approximate />
	<event date = "1840" pop = "800" approximate />
	<event date = "1845" pop = "2000" approximate />
	<event date = "1846" join = "MILWAUKEE" src = "Combinined with Juneautown and Kilbourntown to make Milwaukee"/>
   </history>
  </settlement>
  <settlement id = "MILWAUKEE" lat="43°03'N" long="87°57'W">
   <history>
    <event date = "1846" pop = "10000" realm = "WISCONSIN TERRITORY" name = "Milwaukee" src = "Juneautown, Kilbourntown, and Walker's point incorporated as Milwaukee"/>
	<event date = "1848" realm = "WISCONSIN" src = "Wisconsin made into a state"/>
    <event date = "1850" pop = "20061" src = "First census"/>
    <event date = "1860" pop = "45246"/>
    <event date = "1870" pop = "71440"/>
    <event date = "1880" pop = "115587"/>
    <event date = "1890" pop = "204468"/>
    <event date = "1900" pop = "285315"/>
    <event date = "1910" pop = "373857"/>
    <event date = "1920" pop = "457147"/>
    <event date = "1930" pop = "578249"/>
    <event date = "1940" pop = "587472"/>
    <event date = "1950" pop = "637392"/>
    <event date = "1960" pop = "741324"/>
    <event date = "1970" pop = "717099"/>
    <event date = "1980" pop = "636212"/>
    <event date = "1990" pop = "628088"/>
    <event date = "2000" pop = "596974"/>
    <event date = "2010" pop = "604133" src = "Latest census"/>
   </history>
  </settlement>
 </settlements>
 <realms>
  <realm id = "FRENCH COLONIES">
   <history>
    <event date = "1634" name = "French Colonies" src = "Forts and colonies founded by French explorers"/>
	<event date = "1763" join = "ILLINOIS COUNTRY"/>
   </history>
  </realm>
  <realm id = "ILLINOIS COUNTRY">
   <history>
    <event date = "1763" name = "Illinois Country" src = "British gained authority with Treaty of Paris"/>
	<event date = "1812" join = "ILLINOIS TERRITORY"/>
   </history>
  </realm>
  <realm id = "ILLINOIS TERRITORY">
   <history>
    <event date = "1812" name = "Illinois Territory" src = "U.S. exercises control after War of 1812"/>
	<event date = "1818" join = "WISCONSIN COUNTRY"/>
   </history>
  </realm>
  <realm id = "WISCONSIN COUNTRY">
   <history>
    <event date = "1818" name = "Wisconsin Country" src = "Formed when Illinois was granted statehood"/>
	<event date = "1836" join = "WISCONSIN TERRITORY"/>
   </history>
  </realm>
  <realm id = "WISCONSIN TERRITORY">
   <history>
    <event date = "1836" name = "Wisconsin Territory" src = "Established by U.S. Congress; includes the future states of Wisconsin, Minnesota, Iowa, and parts of the Dakotas"/>
	<event date = "1848" join = "WISCONSIN"/>
   </history>
  </realm>
  <realm id = "WISCONSIN">
   <history>
    <event date = "1848" name = "Wisconsin" src = "Wisconsin granted statehood by U.S."/>
   </history>
  </realm>
</geopolitical>
```