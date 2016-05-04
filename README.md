## INewsSystem

###Basic Components

####GUI
A selection box is displayed on the window for user to select a queue.

The action is triggered once user clicks “select” button for confirmation.

A “stop” button is to stop the operation.

####Scheduler
Once a queue is confirmed to be processed, a scheduler is triggered.

It keeps checking the file change in dedicated queue directory on the ftp server every a constant time interval.

####Xml Parser
Once any file changes are detected, a xml parser then parses those changed files in xml format. CG and prompter has different xml parser.

If the designated element or attribute is changed via xml parser checking, the message is sent to the output.

####Output
For prompter, data is outputted to index file and the data file.

For CG, data is outputted to another system via dedicated comm port.
