import Swal from 'sweetalert2';

export const doNothing = () => {};

function getErrorMsgHtml(msg: string[]): string {
  let text = '<div style="display: block; overflow-y: auto; overflow-x: auto; -ms-overflow-style: -ms-autohiding-scrollbar;">';
  msg.forEach(
    m =>
      (text +=
        '<div style="width: 100%; overflow: visible; white-space: nowrap; padding-bottom: 3px; text-align: left; color: black;">' +
        m +
        '</div>')
  );
  text += '</div>';
  return text;
}

export const showError = (title: string, ...msg: string[]) => {
  Swal.fire({
    icon: 'error',
    title,
    html: getErrorMsgHtml(msg),
    width: 800,
  }).then(doNothing);
};

export const showWarning = (title: string, ...msg: string[]) => {
  Swal.fire({
    icon: 'warning',
    title,
    html: getErrorMsgHtml(msg),
    width: 800,
  }).then(doNothing);
};
