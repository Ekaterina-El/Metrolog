package el.ka.someapp.data.model

import el.ka.someapp.data.model.measuring.MeasuringHistoryItemExecuted

abstract class ListItem {
  abstract var type: TypeListItem
  abstract var value: Any
}

class HeaderItem(title: String) : ListItem() {
  override var type = TypeListItem.HEADER
  override var value = title as Any
}

class ContentItem(history: MeasuringHistoryItemExecuted) : ListItem() {
  override var type = TypeListItem.CONTENT
  override var value = history as Any

}