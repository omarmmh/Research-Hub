let i = 0;
let item = '#author'+i;
$('#addAuthor').click(function (){
    if ($(item).val().trim().length != 0){
        i++
        $('#authors').append("<li class=\"mb-1\" id='userInput"+i.toString()+"'> <input required type=\"text\" class=\"form-control\" id='author"+i.toString()+"' name=\'authors["+i.toString()+"]\' value> </li>");
        item = '#author'+i;
    }
});

$('#deleteAuthor').click(function(){
    if(i > 0) {
        let element = "#userInput" + i;
        $(element).remove();
        i--;
        item = '#author' + i;
    }
});

if($("#result").text() == "Upload was Successful!"){
    showToastSuccess("Upload was Successful!")
}else if($("#result").text() == "Please Upload a file that is PDF format."){
    showToastError("PDF Files Only.")
}else if ($("#result").text() == "File size is bigger than 10MB."){
    showToastError("File Size Bigger Than 10MB")
}