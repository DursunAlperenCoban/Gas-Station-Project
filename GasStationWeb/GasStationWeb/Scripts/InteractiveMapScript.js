const selected = document.querySelector(".selected");
const optionsContainer = document.querySelector(".options-container");

const optionsList = document.querySelectorAll(".option");


selected.addEventListener("click", () => {
    optionsContainer.classList.toggle("active");
    
});


const selected2 = document.querySelector(".selected2");
const optionsContainer2 = document.querySelector(".options-container2");

const optionsList2 = document.querySelectorAll(".option2");

selected2.addEventListener("click", () => {
    optionsContainer2.classList.toggle("active");
});

optionsList2.forEach(o => {
    o.addEventListener("click",() => {
        selected2.innerHTML = o.querySelector("label").innerHTML;
        selected2.id = o.querySelector("input").id;
        optionsContainer2.classList.remove("active");
        getPrices(selected2.id);
    });
});

function getPrices(brand) {
    var id = document.getElementById('distid').innerHTML;
    $.ajax({
        url: '/Home/GetPrices/',
        type: 'POST',
        data: {id: id,brand: brand},
        dataType: 'json',
        beforeSuccess: function () { $(".loader-wrapper").fadeIn("slow"); },
        success: function (data) {
            if ($("#prices") != null)
                $("#prices").remove();
            $(".loader-wrapper").fadeOut("slow");
            $(".price-table").css("display", "flex");
            $.each(data, function (i, pri) {

                $(".price-table").append('<tr id="prices"><td>' + pri.benzin + '</td> <td> ' + pri.motorin + '</td> <td> '+pri.otogaz+'</td></tr>');
            });
           
        },
        error: function (hata, ajaxOptions, thrownError) {
            alert(hata.responseText);
        }
    });
}
