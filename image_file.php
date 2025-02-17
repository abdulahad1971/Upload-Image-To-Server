<?php

$image64 = $_POST["images"];
$decodedImage = base64_decode($image64);

$fileName = time() . '_'. rand(1000,100000). '.jpg' ;
$filePath = 'Images/'. $fileName;

if( file_put_contents($filePath,$decodedImage)){
  echo "image upload successfully";
}
else{
  echo "image upload failed";
}



?>