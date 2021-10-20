# omron-fins
Java implementation of Omron FINS communication protocol

# Documentation

<div style='padding:0.1em; background-color:#E9D8FD; color:#69337A'>
  ## `public interface Cp1lFins`

  ## `void connect() throws Exception`

  Establishes connection with the device

   * **Exceptions:** `Exception` — Error opening connection

  ## `boolean isConnected()`

  Connection status

   * **Returns:** true connected

  ## `void disconnect()`

  Close current connection

  ## `ControllerData getControllerData() throws Exception`

  Controller information data

   * **Returns:** ControllerData
   * **Exceptions:** `Exception` — Error getting ControllerData

  ## `boolean read(int position, int bit) throws Exception`

  Read the state of the indicated bit

   * **Parameters:**
     * `position` — Memory position
     * `bit` — Bit position
   * **Returns:** bit status
   * **Exceptions:** `Exception` — Error reading

  ## `boolean read(CIO cio) throws Exception`

  Read the state of the indicated bit

   * **Parameters:** `cio` — Channel Input Output
   * **Returns:** bit status
   * **Exceptions:** `Exception` — Error reading

  ## `void write(int position, int bit, boolean value) throws Exception`

  Write the status of the indicated input

   * **Parameters:**
     * `position` — Memory position
     * `bit` — Bit position
     * `value` — New state
   * **Exceptions:** `Exception` — Error writing

  ## `void write(CIO cio, boolean value) throws Exception`

  Write the status of the indicated input

   * **Parameters:**
     * `cio` — Channel Input Output
     * `value` — new state
   * **Exceptions:** `Exception` — Exception Error writing

  ## `void setInputListener(InputListener listener)`

   * **Parameters:** `listener` — Interface to notify subscription events

  ## `InputListener getInputListener()`

   * **Returns:** Interface where subscription events are notified

  ## `void subscribe(CIO cio)`

  Adds a subscription to the state change events of the indicated bit

   * **Parameters:** `cio` — Channel Input Output

  ## `boolean unsuscribe(CIO cio)`

  Remove bit subscription

   * **Parameters:** `cio` — Channel Input Output
   * **Returns:** true if there was a subscription

  ## `void subscribe(int position, int bit)`

  Adds a subscription to the state change events of the indicated bit

   * **Parameters:**
     * `position` — Memory position
     * `bit` — Bit position

  ## `void unsuscribe(int position, int bit)`

  Remove bit subscription

   * **Parameters:**
     * `position` — Memory position
     * `bit` — Bit position

  ## `void unsuscribeAll()`

  Remove all bit subscriptions
</div>
