# Introduction #

This file will be used to fill the expedition world map with hundreds of cities, based on the history period being played!

# Useful links #
  * [Europe\_about\_1560](http://upload.wikimedia.org/wikipedia/commons/2/2c/Europe_about_1560.jpg)
  * http://en.wikipedia.org/wiki/Historical_cities

# Details #

```

<geopolitical>
 <settlements>
  <settlement id = "PALOS" lat="37°14'N" long = "6°54'W"/>
   <history>
    <date = "1120" pop = "10" realm = "ALMOHAD" name = "Palos"/>
    <date = "1248" realm = "CASTILE"/>
    <date = "1322" pop = "100"/>
    <date = "1475" pop = "2500" />
    <date = "1550" pop = "700"/>
    <date = "1700" pop = "125"/>
    <date = "1750" pop = "2500"/>
    <date = "2006" pop = "8415"/>
   </history>
  </settlement>
 </settlements>
 <realms>
  <realm id = "ALMOHAD"/>
  <realm id = "CASTILE">
   <history>
    <date = "1035" name = "Castile"/>
    <date = "1230" name = "Castile and Leon"/>
    <date = "1516" join = "SPAIN"/>
   </history>
  </realm>
  <realm id = "LEON">
   <history>
    <date = "910" name = "Leon"/>
    <date = "1230" join = "CASTILE"/>
   </history>
  </realm>
  <realm id = "SPAIN">
   <history>
    <date = "1516" name = "Spain"/>
   </history>
  </realm>
 </realms>

</geopolitical>

```