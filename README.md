# AutoRecycle

Created in an academic Internet on Things (IoT) project course (No. 236333, Technion).
An IoT project includes a device, a mobile application and Azure cloud services.

General Description:
	Our machine classifies home-waste items to 4 categories - Aluminum, Cardboard, Plastic and Glass.
	The device is made of a box sitting on a lifted rail.
	The box has a controlled hatch as its bottom, and under the box's path on the rail are different bins for different types of material.
	In the box there are numerous sensors installed, along with a RaspberryPi and a camera.
	Once an item has been inserted to the box, we use Azure's CV service along with our sensors to determine the material of which the item is made.
	Data of the recycler is accumulated to our app, where the recycler can both check the amount of items s/he had recycled, and instruct which material is the suitable one regarding an unrecognized item.

Implementation Summary:
	The device is controlled by Arudino Uno and RaspberryPi and was implemented in c and python.
	Arduino hardware consists of a manually built metal detector, photoresistors, manually built "Clink"ing device, a sound sensor etc. 
	The mobile app was designed with Android Studio and FireBase and was implemented in Java and XML.
	The Azure cloud service mainly used is Azure's Custom Vision tool.
	Communication between different devices is made using Wi-Fi.

Additional Information:
	See "Project Diagram" and "Project Report" pdf files.
	Visit https://www.youtube.com/watch?v=2X-S_DFzLNc to check out our Project's video.