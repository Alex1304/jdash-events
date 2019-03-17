# JDash Events

[![Maven Central](https://img.shields.io/maven-central/v/com.github.alex1304/jdash-events.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.alex1304%22%20AND%20a:%22jdash-events%22)

A Java library for Geometry Dash events.

# Installation

You can install the latest version with Maven:

```xml
<dependency>
	<groupId>com.github.alex1304</groupId>
	<artifactId>jdash-events</artifactId>
	<version><!-- Latest version number --></version>
</dependency>
```

# How to use it

```java
// Build an authenticated GD client
AuthenticatedGDClient client = GDClientBuilder.create().buildAuthenticated(args[0], args[1]);
// Create the event dispatcher
GDEventDispatcher dispatcher = new GDEventDispatcher();
// Subscribe to any events you want!
dispatcher.on(AwardedLevelAddedGDEvent.class)
		.subscribe(System.out::println);
// In order to dispatch new events, you can either do it manually...
client.getLevelById(10565740)
		.map(AwardedLevelAddedGDEvent::new)
		.subscribe(dispatcher::dispatch);
// ...or automatically via a scanner loop!
// Scanners are capable of refreshing a certain resource in the game at regular intervals
// in order to produce new events.
Collection<GDEventScanner> scanners = new ArrayList<>();
scanners.add(new AwardedSectionScanner());
scanners.add(new DailyLevelScanner());
scanners.add(new WeeklyDemonScanner());
// Create the scanner loop and start scanning for events
GDEventScannerLoop scannerLoop = new GDEventScannerLoop(client, dispatcher, scanners, Duration.ofSeconds(5));
scannerLoop.start();
```

# License

MIT

# Contribute

Issues and Pull requests are more than welcome ! There is no guide that tells how to structure them, but if you explain clearly what you did in your pull request, we will be able to dicuss about it and getting it eventually merged. This is the same for issues, feel free to submit them as long as they are clear.

# Contact

E-mail: mirandaa1304@gmail.com

Discord: Alex1304#9704

Twitter: @gd_alex1304