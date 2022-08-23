package web

object HTML {
  def root(head: String, body: String): String =
    s"<html><head>$head</head><body>$body</body></html>"
}
