////////////////////////////////////////
//
// Help Page sidebar position lock
//

$(document).scroll(function() {
    if ($(document).scrollTop() >= 7638) {
        $(".help__sidebar").removeClass("sidebar__scrolling");
        $(".help__sidebar").addClass("sidebar__bottom");
    } else if ($(document).scrollTop() < 7559 && $(document).scrollTop() > 80) {
        $(".help__sidebar").addClass("sidebar__scrolling");
        $(".help__sidebar").removeClass("sidebar__bottom");
    } else if ($(document).scrollTop() <= 80) {
        $(".help__sidebar").removeClass("sidebar__scrolling");
        $(".help__sidebar").removeClass("sidebar__bottom");
    }
});
