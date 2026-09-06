import { prisma } from "@/lib/prisma";

export async function notifyPriest(params: {
  priestId: string;
  bookingId?: string;
  type: "NEW_BOOKING" | "BOOKING_CANCELLED" | "PAYMENT_RECEIVED";
  message: string;
}) {
  await prisma.notification.create({
    data: {
      priestId: params.priestId,
      bookingId: params.bookingId,
      type: params.type,
      message: params.message,
    },
  });
}
