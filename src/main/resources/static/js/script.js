console.log("This is script file.")

const toggleSidebar=()=>{

    // If true then close
    if($('.sidebar').is(":visible")){
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left","0%")
    }else{
        // If false then show
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left","20%")
    }

};

const search=()=>{
//  console.log("Searching....!!!")
    let query=$("#search-input").val();
    if(query==""){
        $(".search-result").hide();
    }
    else{
        //search
        console.log(query);

        //Sending request to server
        let url = `http://localhost:80/search/${query}`;
        fetch(url).then((response)=>{
            return response.json();
        }).then((data) => {
            //data result
            console.log(data);
            let text = `<div class="list-group">`;

            if (data.length === 0) {
                text += `<p class="list-group-item">No result found</p>`;
            } else {
                data.slice(0, 8).forEach((contact) => {
                    text += `<a href="/user/${contact.cId}/contact" class="list-group-item list-group-item-action">${contact.name}</a>`;
                });
            }

            text += `</div>`;

            $(".search-result").html(text);
            $(".search-result").show();
        });

       
    }
};



function toggleSettings() {
	var settingsLinks = document.getElementById("settings-links");
	settingsLinks.style.display = (settingsLinks.style.display === "none") ? "block" : "none";
}