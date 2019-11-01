# BTMKSim
BTMKSim is a project for using sensors of Android phone to simulate the movement of mouse and input of keyboard on PC. Concretely, it 1) uses [GAME_ROTATION_VECTOR](https://developer.android.google.cn/reference/android/hardware/Sensor.html?hl=en#TYPE_GAME_ROTATION_VECTOR) to calculate [Azimuth and Roll](https://developer.android.google.cn/reference/android/hardware/SensorManager#getOrientation(float%5B%5D,%20float%5B%5D)), 2) uses [Azimuth and Roll](https://developer.android.google.cn/reference/android/hardware/SensorManager#getOrientation(float%5B%5D,%20float%5B%5D)) values to calculate the relative positions of mouse on screen of PC, 3) uses buttons to represent keys and 4) communicates with PC via Bluetooth and simulate the movement of mouse and input of keyboard.



## Dependencies
### PC Part
- Python 3
- [pybluez](https://github.com/pybluez/pybluez) (If your system is win10, pyblue may have some problems on compatibility. [PyBluez-win10](https://pypi.org/project/PyBluez-win10) is a great module for win10.)
- [pyautogui](https://github.com/asweigart/pyautogui)

### Android Part
- Nothing, but please make sure your phone supports bluetooth.

## Usage
1. Make sure your phone and target PC are paired via Bluetooth.
2. Run the PC_part python script *BTMKSim.py*.  
`python BTMKSim.py`

3. Press CONNECTION button in app to get the list of paired devices and press the name of target PC.
4. Go back to launcher page and press GRV CONTROL button to go to control page.
5. Press RESET MOUSE to set the center of your orientation window. You may try other buttons as you like. :)

## Q & A
**Q**: Can I drag or scroll when simulating mouse?  
**A**: Unfortunately no. These features has not been implemented. :(

**Q**: Can I keep entering characters by long clicking the button on app?  
**A**: Unfortunately no. I have not found a way to implement this feature in android. :(

**Q**: Can I use this project to play FPS game?  
**A**: Unfortunately no. Firstly, FPS game cannot get the movement of mouse by using [pyautogui](https://github.com/asweigart/pyautogui). Maybe we need lower level Windows APIs to do that. Secondly, FPS game may be unable to get the input of keyboard by using [pyautogui](https://github.com/asweigart/pyautogui) if you use current version. The good news is that someone has fixed this problem in [this pull request](https://github.com/asweigart/pyautogui/pull/299).

**Q**: What exactly can this project simulate rightnow?  
**A**: 1) Movement of mouse; 2)left and right click; 3)typing character 'w', left and right arrow.

## Tips
- Left and right click massage might not be sent if the orientation of your phone changed a little bit fast. This is a problem to be sloved.
- You may want to PAUSE, i.e. stop sending orientation massage for movement of mouse, when you try to type some keys.

