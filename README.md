
<h1 align="center">
  <p>AreaResetterPro</p>
  <img width=20% src="https://i.imgur.com/vVXzVAR.png" alt="AreaResetterPro Banner">
</h1>

<h4 align="center">A light-weight Minecraft Plugin for the PaperMC server fork.</h4>
<hr>

<p align="center">
  <a href="#about">About</a>
  •
  <a href="#features">Features</a>
  •
  <a href="#dependencies">Dependencies</a>
  •
  <a href="#installation-guide">Installation Guide</a>
  •
  <a href="#permissions">Permissions</a>
  •
  <a href="#commands">Commands</a>
  •
  <a href="#support">Support</a>
  •
  <a href="#contributions">Contributions</a>
  •
  <a href="#license">License</a>
</p>

---

> [!Note]
>
> #### ***AreaResetterPro requires FastAsyncWorldEdit to operate!***

## About

AreaResetterPro is a versatile Minecraft plugin, which enables you to create, manage and reset predefined areas of your Minecraft server. It's built to support multiple worlds and offers a in-game GUI for easy administration.

## Features

- **Efficient Area Resets**: Reseting an Area is done asynchronously, so your servers main-thread can focus on handling other important tasks!
- **Simplicity**: The Plugin is designed to work straight out of the box.
- **Scheduled Resets**: AreaResetterPro allows for automated resets. This feature can be disabled in the 'config.yml' file.
- **Multi-World Support**: Operating across different worlds of your server is no problem.
- **Customization**: The Plugin allows for customization of nearly every message you will ever receive.

## Dependencies

- `FastAsyncWorldEdit`: Make sure to have the latest version of FastAsyncWorldEdit installed on your Minecraft server. Otherwise AreaResetterPro will not be able to function!
- `PlaceholderAPI`: Not required! Allows to use placeholders to show how much time until an area is reset the next time. Usage: %arearesetterpro_[areaName]%

## Installation Guide

1. Download AreaResetterPro from [SpigotMC](https://www.spigotmc.org/resources/arearesetterpro.109372/).
2. Place the downloaded file in your server's `plugins` folder.
3. Restart your server to load the plugin.

## Permissions

- `arearesetterpro.reload`: Allows players to reload the plugins configuration files.
- `arearesetterpro.tool`: Allows players to obtain and use the AreaReseterPro Tool.
- `arearesetterpro.getpos`: Allows players to receive the currently set positions.
- `arearesetterpro.setspawnpoint`: Allows players to set the spawn-point.
- `arearesetterpro.getspawnpoint`: Allows players to receive the currently set spawn-point.
- `arearesetterpro.create`: Allows players to create an new area object.
- `arearesetterpro.remove`: Allows players to remove an area object.
- `arearesetterpro.reset`: Allows players to reset an area.
- `arearesetterpro.menu`: Allows players to use the plugins menu.
- `arearesetterpro.help`: Allows players to used the help command.

## Commands

- `/arp_reload`: Reloads the configuration files.
- `/arp_tool`: Provides the caller with the AreaResetterPro Tool.
- `/arp_getpos`: Provides the player with the currently set positions.
- `/arp_setspawnpoint`: Will set the spawn-point to the players location.
- `/arp_getspawnpoint`: Provides the player with the currently set spawn-point.
- `/arp_create [AreaName]`: Will create a new area object with the corresponding name.
- `/arp_remove [AreaName]`: Will delete the corresponding area object, if it exists.
- `/arp_reset [AreaName]`: Will reset the corresponding area object, if it exists.
- `/arp_menu`: Opens the plugins GUI.
- `/arp_help`: Provides players with a simple help text.

## Support

Join my [Discord Server](https://discord.gg/QNz9MdnmGK) for assistance, suggestions, or discussions regarding AreaResetterPro.

## Contributions

### How to Contribute
1. Fork and star the repository.
2. Create a branch for your changes.
3. Commit and push your changes.
4. Submit a pull request with a clear description of your improvements.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center"> 
<a href='https://www.paypal.com/paypalme/lgndluke' target='_blank'><img height='50' src='https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/PayPal.svg/2560px-PayPal.svg.png' alt='Support Me via PayPal'/></a>
</div>
