#GuitarPi
A presentation of this project recored at [J-Fall (2018)](https://www.youtube.com/watch?v=8-SkMg9-jcI) and Devoxx (2019) available soon.

##Open source projects used
* [Dropwizard application](https://www.dropwizard.io/) now replaced with [quarkus](https://quarkus.io)
* A patched version of [jfugue](http://www.jfugue.org/) for paring music xml (included in this repository)
* [Pi4J](https://pi4j.com/) for controlling the raspberry GPIO ports
* PCA9685 driver obtained form [github.com/OlivierLD/raspberry-coffee](https://github.com/OlivierLD/raspberry-coffee/blob/master/I2C.SPI/src/i2c/servo/pwm/PCA9685.java) (no longer available)

##Resouces used in presentation:
* Youtube: [Dust In The Wind by Kansas on a 12 String](https://www.youtube.com/watch?v=B9DttjXo9Cs) (23 jan. 2009)
* Youtube: [Guitar-playing robot](https://www.youtube.com/watch?v=jC2VB-5EnUs) (13 sep. 2010)
* Youtube: [Bach. Toccata and Fugue.(Guitar-Robot)](https://www.youtube.com/watch?v=cB0WPSGge3k) (27 apr. 2015)
* Youtube: [Little Talks Guitar Cover by Lego Mindstorms EV3](https://www.youtube.com/watch?v=cXgB3lIvPHI) (10 mrt. 2015)
* Youtube: [Robot Guitar - Arduino - Jessica](https://www.youtube.com/watch?v=DBqdnujBSpI) (3 jun. 2015)


##Build UI:
in folder `/frontend/guitar` call: `ng build --env=prod --deploy-url /ui --base-href /ui`

