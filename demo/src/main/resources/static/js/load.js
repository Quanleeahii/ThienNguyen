document.addEventListener("click", function(e) {
    let pageLink = e.target.closest(".custom-pagination a");
    if (pageLink && !pageLink.classList.contains("disabled")) {
        e.preventDefault();
        let url = pageLink.href;
        let grid = document.getElementById("campaign-grid");
        let pagination = document.getElementById("pagination-box");
        grid.style.opacity = "0.4";
        grid.style.transition = "opacity 0.3s ease";
        fetch(url)
            .then(response => response.text())
            .then(html => {
                let parser = new DOMParser();
                let virtualDoc = parser.parseFromString(html, "text/html");
                let newGrid = virtualDoc.getElementById("campaign-grid");
                let newPagination = virtualDoc.getElementById("pagination-box");
                if (newGrid) {
                    grid.innerHTML = newGrid.innerHTML;
                }
                if (pagination) {
                    if (newPagination) {
                        pagination.innerHTML = newPagination.innerHTML;
                        pagination.style.display = "flex";
                    } else {
                        pagination.style.display = "none";
                    }
                }
                grid.style.opacity = "1";
                window.history.pushState({}, "", url);
                grid.scrollIntoView({ behavior: "smooth", block: "start" });
            })
            .catch(error => {
                console.error("Lỗi AJAX Phân trang:", error);
                grid.style.opacity = "1";
                window.location.href = url;
            });
    }
});