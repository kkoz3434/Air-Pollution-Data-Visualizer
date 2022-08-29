import 'package:adpv_frontend/Views/AdminPage/utils.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

showAlertDialog(BuildContext context, String warningTitle,
    String warningContent, onAcceptFunction) {

  _onProceedPressed() {
    onAcceptFunction();
    Navigator.of(context).pop();
  }

  final Widget continueButton = ElevatedButton(
    onPressed: _onProceedPressed,
    child: const Text("Proceed"),
  );

  final AlertDialog alert = AlertDialog(
    title: Text(warningTitle),
    content: Text(warningContent),
    actions: [continueButton, cancelButton(context)],
  );

  showDialog(
    context: context,
    builder: (BuildContext context) => alert,
  );
}
