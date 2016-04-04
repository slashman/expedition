#How to contribute

It's important to us that you feel you can contribute towards the evolution of Expedition. This can take many forms: from helping to fix bugs or improve the docs, to adding in new features to the source. This guide should help you in making that process as smooth as possible.

##Reporting issues

GitHub Issues is the place to report bugs you may have found. When submitting a bug please do the following:

**1. Search for existing issues.** Your bug may have already been fixed or addressed in a development branch version of Expedition, so be sure to search the issues first before putting in a duplicate issue.

**2. Not sure if it's a bug?.** Then please contact us. If something is blatantly wrong then post it to github. But if you feel it might just be because you're not sure of expected behaviour, then it might save us time, and get you a response faster, if you contact us.

**3. Create an isolated and reproducible test case.** If you are reporting a bug, make sure you also have a minimal, runnable, code example that reproduces the problem you have.

**4. Share as much information as possible.** Include your OS, version of the game, steps to reproduce, etc. "X isn't working!!!1!" will probably just be closed.


##Serf and Expedition

It's important to understand that internally Expedition uses SERF as a game engine. It's possible you may find a bug that is generated on the SERF level rather than Expedition. You're welcome to still report the issue of course, but if you get a reply saying we think it might be a SERF issue this is what we're talking about :)


##Making Changes

Make your changes and submit a Pull Request:

- **Send Pull Requests to the `master` branch.** All Pull Requests must be sent to the `master` branch

- **Never commit new builds.** Please do not commit new builds or your PR will be closed. 

- **Only commit relevant changes.** Don't include changes that are not directly relevant to the fix you are making. The more focused a PR is, the faster it will get attention and be merged. Extra files changing only whitespace or trash files will likely get your PR closed.


##Coding style contributions

Expedition source code is a mess. Coding style contributions are welcome BUT should be made as a separate PR. We will eventually reach a point where the code is sufficiently organized and we will be able to reject PRs which are just personal preferences.

Note that PRs should ideally fix bugs, fix documentation or add features. No changes for the sake of change.

##Code Style Guide

- Use tab characters for tabs.

- No trailing whitespace, blank lines should have no whitespace.

- Follow conventions already in the code.

Thanks to Chad for creating the original Pixi.js Contributing file which was adapted for Phaser, which we used as a basis.
