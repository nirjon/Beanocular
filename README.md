# Beanocular
A simple Android application that accesses LightBlue Beans using the Bean-Android SDK (downloaded 10/17/2015). The app scans for nearby Beans and connects to all of them. Each bean is handled in a separate thread which simply reads accelerometer values and shows them on a TextView.   

#Comments (10/19/2015)
- Works with Nexus 5 (Marshmallow). Target SDK = 22 (i.e. old style PERMISSIONS)
- Bean was loaded with the AccelerationReader program (but it should not matter what code is there)
- Used an ugly way to display accelerations on screen. GUI is not updated in real-time.
- Used an odd way to determine which Bean is sending data (when multiple beans are connected)
- It scans for Beans and connects to all of them. There should have been an option to choose. 
- Have not tested with multiple beans, yet. 
