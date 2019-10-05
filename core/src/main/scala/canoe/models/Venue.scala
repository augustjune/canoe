package canoe.models

/** This object represents a venue.
  *
  * @param location       Venue location
  * @param title          Name of the venue
  * @param address        Address of the venue
  * @param foursquareId   Foursquare identifier of the venue
  * @param foursquareType Foursquare type of the venue.
  *                       For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.
  */
final case class Venue(location: Location,
                       title: String,
                       address: String,
                       foursquareId: Option[String],
                       foursquareType: Option[String])
