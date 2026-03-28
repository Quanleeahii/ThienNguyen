/* ==========================================
   TUYỆT CHIÊU AJAX: CHUYỂN TRANG KHÔNG RELOAD
========================================== */
document.addEventListener("click", function(e) {
    let pageLink = e.target.closest(".custom-pagination a");

    if (pageLink && !pageLink.classList.contains("disabled")) {
        e.preventDefault();
        let url = pageLink.href;
        let grid = document.getElementById("campaign-grid");
        let pagination = document.getElementById("pagination-box");

        grid.style.opacity = "0.4";
        grid.style.transition = "0.3s";

        fetch(url)
            .then(response => response.text())
            .then(html => {
                let parser = new DOMParser();
                let virtualDoc = parser.parseFromString(html, "text/html");

                let newCampaigns = virtualDoc.getElementById("campaign-grid").innerHTML;
                let newPagination = virtualDoc.getElementById("pagination-box").innerHTML;

                grid.innerHTML = newCampaigns;
                pagination.innerHTML = newPagination;
                grid.style.opacity = "1";

                window.history.pushState({}, "", url);
                document.querySelector(".filter-tabs").scrollIntoView({ behavior: "smooth" });
            })
            .catch(error => {
                console.error("Lỗi AJAX:", error);
                grid.style.opacity = "1";
            });
    }
});