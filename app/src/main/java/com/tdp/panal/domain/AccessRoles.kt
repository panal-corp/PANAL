package com.tdp.panal.domain

/* ============================================================
   AccessRoles.kt
   Transcripción de Proyecto 2/Services/AccessRoles.cs (.NET).

   Define los cuatro accesos de Panal y qué puede hacer cada uno:
   - Cliente:       compra y devuelve solo a nombre de su cuenta.
   - Empleado:      punto de venta, solo ventas. Usa llave de rol.
   - Trabajador:    ventas y devoluciones de clientes. Llave de rol.
   - Administrador: todo, más reportes, almacén, cuentas y personal.
   ============================================================ */
object AccessRoles {
    const val SUPERIOR = "Superior"
    const val ADMINISTRATOR = "Administrador"
    const val WORKER = "Trabajador"
    const val EMPLOYEE = "Empleado"
    const val CUSTOMER = "Cliente"

    val ALL: List<String> = listOf(SUPERIOR, ADMINISTRATOR, WORKER, EMPLOYEE, CUSTOMER)

    const val MAX_ROLE_KEY_LENGTH = 40
    const val MIN_ROLE_KEY_LENGTH = 4

    fun isValid(role: String?): Boolean = role in ALL

    /* Las cuentas anteriores a los roles no tienen rol guardado:
       en .NET se interpretan como Administrador, y aquí igual. */
    fun normalize(role: String?): String = if (isValid(role)) role!! else ADMINISTRATOR

    /* Todos los roles pueden abrir el punto de venta;
       el cliente solo opera a su propio nombre. */
    fun canUsePointOfSale(role: String?): Boolean = true

    fun canSell(role: String?): Boolean = canUsePointOfSale(role)

    /* El empleado no recibe devoluciones; el cliente solo las suyas. */
    fun canReceiveCustomerReturns(role: String?): Boolean =
        normalize(role) in listOf(SUPERIOR, ADMINISTRATOR, WORKER, CUSTOMER)

    /* Abastecimiento y devoluciones al proveedor: solo administración. */
    fun canManageSupply(role: String?): Boolean = normalize(role) in listOf(ADMINISTRATOR, SUPERIOR)

    /* Inicio general (comprobantes y efectos): solo administración. */
    fun canSeeOverview(role: String?): Boolean = normalize(role) in listOf(ADMINISTRATOR, SUPERIOR)

    /* Almacén y cuentas: solo administración. */
    fun canSeeRecords(role: String?): Boolean = normalize(role) in listOf(ADMINISTRATOR, SUPERIOR)

    /* Reportes con gráfica o tabulación: solo administración. */
    fun canSeeReports(role: String?): Boolean = normalize(role) in listOf(ADMINISTRATOR, SUPERIOR)

    /* Alta, cambio de rol y baja de cuentas: solo administración. */
    fun canManageUsers(role: String?): Boolean = normalize(role) in listOf(ADMINISTRATOR, SUPERIOR)

    /* El cliente opera con su propio nombre, sin elegir a otra persona. */
    fun usesOwnCustomerName(role: String?): Boolean = normalize(role) == CUSTOMER

    /* La doble llave: Trabajador y Empleado entran con
       usuario + contraseña + llave de rol. */
    fun requiresRoleKey(role: String?): Boolean =
        normalize(role) in listOf(SUPERIOR, ADMINISTRATOR, WORKER, EMPLOYEE)

    fun describe(role: String?): String = when (normalize(role)) {
        SUPERIOR -> "Gestión temporal de administradores y acceso completo."
        ADMINISTRATOR -> "Acceso completo, reportes, almacén, cuentas y personal."
        WORKER -> "Punto de venta: ventas y devoluciones de clientes."
        EMPLOYEE -> "Punto de venta: solo registro de ventas."
        else -> "Compras y devoluciones a nombre de la propia cuenta."
    }
}
