<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>HomePage</title>
</head>
<body>
    <p>Insert Building</p>
    <form id="addBuildingForm" action="buildingManager" method="post">
        <label>Name: <input type="text" name="name" required></label><br>
        <label>Address: <input type="text" name="address" required></label><br>
        <label>Capacity: <input type="number" name="capacity" required></label><br>
        <label>OpeningTime: <input type="time" name="openingTime" required></label><br>
        <label>ClosingTime: <input type="time" name="closingTime" required></label><br>
        <input type="hidden" id="counter" name="counter" value="0">
        <div id="departmentForm"></div>
        <input type="submit" value="Submit">



    </form>
    <button onclick="addDepartment(event)">Add department</button>
    <button onclick="removeDepartment()">Remove department</button>
    <!--<button onclick="submitForm()">Submit</button>-->
    <script>
        let departmentCounter = 0;

        function addDepartment(event) {
            if(departmentCounter < 20) {
                event.preventDefault();
                departmentCounter++;
                //let form = document.getElementById("addBuildingForm");

                //let departmentForm = document.createElement("div");

                let departmentForm = document.getElementById("departmentForm");

                let nameInput = document.createElement("input")
                nameInput.type = "text";
                nameInput.name = "dep" + departmentCounter;
                nameInput.placeholder = "Department Name";
                nameInput.setAttribute("required", "");

                let surplusInput = document.createElement("input");
                surplusInput.type = "number";
                surplusInput.name = "surplus" + departmentCounter;
                surplusInput.placeholder = "Surplus";
                surplusInput.setAttribute("required", "");

                departmentForm.appendChild(nameInput);
                departmentForm.appendChild(surplusInput);
                departmentForm.append(document.createElement("br"));
                //form.appendChild(departmentForm);

                //update value in hidden input
                document.getElementById("counter").value = departmentCounter;

            }


        }

        function removeDepartment() {
            if(departmentCounter > 0) {
                departmentCounter--;
                let departmentForm = document.getElementById("departmentForm");
                departmentForm.lastChild.remove();
                departmentForm.lastChild.remove();
                departmentForm.lastChild.remove();

                document.getElementById("counter").value = departmentCounter;
            }
        }

        function submitForm() {
            let form = document.getElementById("addBuildingForm");

        }
    </script>
</body>
</html>
