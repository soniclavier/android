# project repository for hilltop vehicle tracking android app

#### How to clone

1.Change directory to the folder you want the project to be setup
```
git clone https://github.com/soniclavier/android.git
```
This will create a folder `android`

2.In android studio, click open project and select the folder android.


### How to update the code base
1.Once someone make any change to code base, you can get the lastest one by:
```
cd 'your_folder_containg_android'
cd android
git pull
```
2.Refresh workspace

### How to commit/add code
```
cd android
git status                   //this will show all the new files added or modified
git add 'file_name'          //this is option1, where you can add only the files that you changed. repeat this for all modified files
git add .                    //this will add all modified files for commit
git commit                   //after this command, you have to write a description of what are the changes that you are committing now
git push                     //this will push the changes on to github server
```


