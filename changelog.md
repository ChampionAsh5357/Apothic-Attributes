## 1.1.4
* Fixed players losing the ability to fly when switching between Creative mode and Spectator mode.

## 1.1.3
* Fixed typo in `GameTypeMixin` causing players to still lose attribute-provided flight when switching back from Creative to Survival.

## 1.1.2
* Fixed the Flight effect not working on relog or when switching game modes.

## 1.1.1
* No longer modifies the flying state when the player is in spectator mode.

## 1.1.0
* Added support for Boolean attributes, which only have an Enabled or Disabled state.
  * A boolean attribute is enabled if the current value is > 0, and disabled if the value == 0.
  * Only two modifiers will display properly for boolean attributes:
    * An Addition +1 modifier will display as "Enables X"
    * A Multiply Total -1 modifier will display as "Forcibly Disables X"
    * Other modifiers will display as "Invalid X", and should not be used for Boolean attributes.
* Added the Elytra Flight attribute.
* Added the Creative Flight attribute.
* Added the Flying mob effect, which grants Creative Flight.
* Made Fall Damage scale with Gravity. If your Gravity is below 0.01 (the amount slow falling reduces it to), fall damage is prevented entirely.

## 1.0.4
* Removed forge dependency line from the mods.toml and marked as Forge and NeoForge for CF.
  * The dependency will be added back and the Forge marker will be removed once CF supports Neo correctly.

## 1.0.3
* Made description convention use `Attribute#getDescriptionId` as the base instead of the registry name.
  * Description keys will now be equal to the normal key with `.desc` appended in all cases.

## 1.0.2
* Updated armor computations so damage values < 20 are much closer to vanilla.
  * Old Graph: https://i.imgur.com/3yEnTyi.png
  * New Graph: https://i.imgur.com/2OHQhgp.png

## 1.0.1
* Added back the code which updates the base value of step height on login.

## 1.0.0
* Initial Release